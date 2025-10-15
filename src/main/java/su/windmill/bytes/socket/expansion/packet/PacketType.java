package su.windmill.bytes.socket.expansion.packet;

import su.windmill.bytes.codec.Codec;
import su.windmill.bytes.util.Assertions;
import su.windmill.bytes.util.Key;

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

    public static <E extends Packet> PacketType<E> serverbound(String key, Codec<E> codec) {
        return new PacketType<>(Flow.SERVERBOUND, Key.key("serverbound_" + key), codec);
    }

    public static <E extends Packet> PacketType<E> clientbound(String key, Codec<E> codec) {
        return new PacketType<>(Flow.CLIENTBOUND, Key.key("clientbound_" + key), codec);
    }


}
