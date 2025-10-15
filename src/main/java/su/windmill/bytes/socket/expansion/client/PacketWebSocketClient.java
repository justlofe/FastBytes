package su.windmill.bytes.socket.expansion.client;

import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.codec.Codecs;
import su.windmill.bytes.codec.context.DecodeContext;
import su.windmill.bytes.socket.ListenerService;
import su.windmill.bytes.socket.MessageWriter;
import su.windmill.bytes.socket.client.AbstractWebSocketClient;
import su.windmill.bytes.socket.expansion.ResponseTimeoutException;
import su.windmill.bytes.socket.expansion.packet.*;
import su.windmill.bytes.socket.listener.context.ContextType;
import su.windmill.bytes.util.Assertions;
import su.windmill.bytes.util.Key;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@SuppressWarnings("unchecked")
public class PacketWebSocketClient extends AbstractWebSocketClient implements PacketClient {

    public static final String PACKET_LISTENER = "packet_listener";
    public static final int DEFAULT_PACKET_TIMEOUT_SECONDS = 30;

    private final PacketRegistry packetRegistry;
    private final long packetTimeoutMillis;
    private final ScheduledExecutorService scheduledExecutorService;

    private final Map<UUID, WaitingResponse> waitingResponse = new HashMap<>();

    private final Map<Key, PacketWithoutResponseListener<?>> listenersWithoutResponse = new HashMap<>();
    private final Map<Key, PacketWithResponseListener<?, ?>> listenersWithResponse = new HashMap<>();

    public PacketWebSocketClient(URI uri) {
        this(
                uri,
                Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors()),
                new ListenerService(),
                new PacketRegistry(),
                DEFAULT_PACKET_TIMEOUT_SECONDS * 1000L
        );
    }

    public PacketWebSocketClient(URI uri, ScheduledExecutorService executorService, ListenerService listenerService, PacketRegistry packetRegistry, long packetTimeoutMillis) {
        super(uri, executorService, listenerService);
        Assertions.between(packetTimeoutMillis, 1L, Long.MAX_VALUE - 1);

        this.packetRegistry = packetRegistry;
        this.packetTimeoutMillis = packetTimeoutMillis;
        this.scheduledExecutorService = executorService;

        addListener(
                Key.key(PACKET_LISTENER),
                ContextType.MESSAGE,
                ctx -> {
                    if(ctx.textMessage().isPresent() ) return;
                    receive(ctx.message());
                }
        );
    }

    public <P extends PacketWithoutResponse> void setPacketListener(PacketType<P> type, PacketWithoutResponseListener<P> listener) {
        Assertions.notNull(type, "type");
        Assertions.notNull(listener, "listener");

        listenersWithoutResponse.put(
                type.key(),
                listener
        );
    }

    public <R extends PacketWithoutResponse, P extends PacketWithResponse<R>> void setPacketListener(PacketType<P> type, PacketWithResponseListener<R, P> listener) {
        Assertions.notNull(type, "type");
        Assertions.notNull(listener, "listener");

        listenersWithResponse.put(
                type.key(),
                listener
        );
    }

    public final PacketRegistry packetRegistry() {
        return packetRegistry;
    }

    @Override
    public <P extends PacketWithoutResponse> void sendWithoutResponse(P packet) {
        if(packet instanceof PacketWithResponse<?> packetWithResponse) {
            sendWithResponse(packetWithResponse);
            return;
        }

        checkTypeValidity(packet.type());

        submit(packet, UUID.randomUUID(), false);
    }

    @Override
    public <R extends PacketWithoutResponse, P extends PacketWithResponse<R>> CompletableFuture<R> sendWithResponse(P packet) {
        PacketType<?> type = packet.type();
        checkTypeValidity(type);

        UUID uuid = UUID.randomUUID();
        submit(packet, uuid, false);

        CompletableFuture<R> future = new CompletableFuture<>();

        this.waitingResponse.put(uuid, new WaitingResponse(
                uuid,
                (CompletableFuture<PacketWithoutResponse>) future,
                System.currentTimeMillis()
        ));

        scheduledExecutorService.schedule(() -> cancelIfNotFinished(uuid), packetTimeoutMillis, TimeUnit.MILLISECONDS);

        return future;
    }

    private void cancelIfNotFinished(UUID uuid) {
        WaitingResponse waitingResponse = this.waitingResponse.get(uuid);
        if(waitingResponse == null) return;
        waitingResponse.future().completeExceptionally(new ResponseTimeoutException(waitingResponse.uuid()));
    }

    private void checkTypeValidity(PacketType<?> type) {
        if(type.flow() != PacketType.Flow.SERVERBOUND) throw new IllegalArgumentException("packet flow is not serverbound!");
        else if(!packetRegistry.registered(type)) throw new IllegalArgumentException("packet with this type is not registered in PacketRegistry");
    }

    private void submit(Packet packet, UUID uuid, boolean response) {
        scheduledExecutorService.execute(() -> send((MessageWriter) buffer -> {
            PacketType<Packet> type = (PacketType<Packet>) packet.type();

            // headers: id, type and uuid
            buffer.writeUTF8(type.key().value());
            buffer.writeBoolean(response);
            Codecs.UUID.encode(uuid, buffer);

            // packet itself
            try {
                type.codec().encode(packet, buffer);
            }
            catch (Throwable throwable) {
                error(throwable);
            }
        }));
    }

    private void receive(FastBuffer buffer) {
        DecodeContext context = DecodeContext.of(buffer);

        String id = buffer.readUTF8();
        boolean response = buffer.readBoolean();
        UUID uuid = Codecs.UUID.decode(context);

        PacketType<? extends Packet> type = packetRegistry.getType(Key.key(id));
        if(type == null) {
            throw new RuntimeException("unknown packet type \"" + id + "\". does something changed after validating connection? ");
        }

        Packet packet = ((PacketType<Packet>) type).codec().decode(context);

        // handle response
        if(response) {
            WaitingResponse waitingResponse = this.waitingResponse.remove(uuid);
            if(waitingResponse == null) return;
            waitingResponse.future().complete((PacketWithoutResponse) packet);
            return;
        }

        if(packet instanceof PacketWithResponse<?> packetWithResponse) {
            PacketWithResponseListener<?, ?> listener = listenersWithResponse.get(packet.type().key());
            if(listener != null) {
                PacketWithoutResponse response0 = ((PacketWithResponseListener<?, PacketWithResponse<?>>) listener).call(this, packetWithResponse);
                submit(response0, uuid, true);
            }
        }
        else {
            PacketWithoutResponseListener<?> listener = listenersWithoutResponse.get(packet.type().key());
            if(listener != null) ((PacketWithoutResponseListener<PacketWithoutResponse>) listener).call(this, (PacketWithoutResponse) packet);
        }
    }

    private record WaitingResponse(UUID uuid, CompletableFuture<PacketWithoutResponse> future, long creationTime) {

    }

}
