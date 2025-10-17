package su.windmill.bytes.socket.expansion.packet;

import su.windmill.bytes.codec.Codec;
import su.windmill.bytes.util.Assertions;
import su.windmill.bytes.util.Key;

/**
 * PacketType describes flow, packet id and codec for this packet
 * @param flow packet flow (destination in other words)
 * @param key unique id of packet
 * @param codec codec
 * @param <E>
 */
public record PacketType<E extends Packet>(Flow flow, Key key, Codec<E> codec) {

    public PacketType {
        Assertions.notNull(flow, "flow");
        Assertions.notNull(key, "key");
        Assertions.notNull(codec, "codec");
    }

    public enum Flow {
        CLIENTBOUND,
        SERVERBOUND
    }

    /**
     * Creates serverbound packet
     * @param key unique id ("serverbound_" will be added in the start)
     * @param codec codec
     * @return PacketType
     * @param <E> packet type
     */
    public static <E extends Packet> PacketType<E> serverbound(String key, Codec<E> codec) {
        return new PacketType<>(Flow.SERVERBOUND, Key.key("serverbound_" + key), codec);
    }

    /**
     * Creates clientbound packet
     * @param key unique id ("clientbound" will be added in the start)
     * @param codec codec
     * @return PacketType
     * @param <E> packet type
     */
    public static <E extends Packet> PacketType<E> clientbound(String key, Codec<E> codec) {
        return new PacketType<>(Flow.CLIENTBOUND, Key.key("clientbound_" + key), codec);
    }


}
