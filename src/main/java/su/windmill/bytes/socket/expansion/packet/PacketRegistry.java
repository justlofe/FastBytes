package su.windmill.bytes.socket.expansion.packet;

import su.windmill.bytes.socket.expansion.handshake.ClientboundHandshakePacket;
import su.windmill.bytes.socket.expansion.handshake.ServerboundHandshakePacket;
import su.windmill.bytes.util.Key;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PacketRegistry {

    public static final PacketType<ServerboundHandshakePacket> SERVERBOUND_HANDSHAKE = PacketType.serverbound(
            "handshake",
            ServerboundHandshakePacket.CODEC
    );

    public static final PacketType<ClientboundHandshakePacket> CLIENTBOUND_HANDSHAKE = PacketType.clientbound(
            "handshake",
            ClientboundHandshakePacket.CODEC
    );

    private final Map<Key, PacketType<? extends Packet>> TYPES = new HashMap<>();
    private boolean compiled = false;
    private final long hash;

    public PacketRegistry() {
        register(SERVERBOUND_HANDSHAKE, CLIENTBOUND_HANDSHAKE);
        initialize(this::register);
        compiled = true;

        Object[] hashObjects = new Object[TYPES.size() + 1];
        hashObjects[0] = TYPES.size();
        int i = 1;
        for (PacketType<? extends Packet> type : TYPES.values()) {
            hashObjects[i++] = type.key().value();
        }
        hash = Arrays.hashCode(hashObjects);
    }

    public long hash() {
        if(!compiled) throw new RuntimeException("hash is not compiled yet!");
        return hash;
    }

    public final boolean registered(PacketType<?> type) {
        return TYPES.get(type.key()) != null;
    }

    public final PacketType<? extends Packet> getType(Key key) {
        return TYPES.get(key);
    }

    @SafeVarargs
    private void register(PacketType<? extends Packet>... types) {
        if(compiled) throw new UnsupportedOperationException("late try to register packet - registry already compiled");
        for (PacketType<? extends Packet> type : types) {
            Key key = type.key();
            if(TYPES.get(key) != null) throw new IllegalArgumentException("type with same key already exists");
            TYPES.put(key, type);
        }
    }

    public void initialize(Consumer<PacketType<? extends Packet>> registrar) {
    }

}
