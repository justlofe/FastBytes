package su.windmill.bytes.socket.expansion.packet;

import su.windmill.bytes.socket.expansion.handshake.ClientboundHandshakePacket;
import su.windmill.bytes.socket.expansion.handshake.ServerboundHandshakePacket;
import su.windmill.bytes.util.Key;

import java.util.HashMap;
import java.util.Map;

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

    public PacketRegistry() {
        register(SERVERBOUND_HANDSHAKE, CLIENTBOUND_HANDSHAKE);
    }

    public boolean registered(PacketType<?> type) {
        return TYPES.get(type.key()) != null;
    }

    public PacketType<? extends Packet> getType(Key key) {
        return TYPES.get(key);
    }

    @SafeVarargs
    public final PacketRegistry register(PacketType<? extends Packet>... types) {
        for (PacketType<? extends Packet> type : types) {
            Key key = type.key();
            if(TYPES.get(key) != null) throw new IllegalArgumentException("type with same key already exists");
            TYPES.put(key, type);
        }
        return this;
    }

}
