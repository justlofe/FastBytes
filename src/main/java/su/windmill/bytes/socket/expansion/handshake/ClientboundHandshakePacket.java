package su.windmill.bytes.socket.expansion.handshake;

import su.windmill.bytes.codec.Codec;
import su.windmill.bytes.socket.expansion.packet.PacketWithoutResponse;
import su.windmill.bytes.socket.expansion.packet.PacketType;

public record ClientboundHandshakePacket(Answer answer) implements PacketWithoutResponse {

    public static final Codec<ClientboundHandshakePacket> CODEC = Codec.fixed(
            (encodable, buffer) -> buffer.writeInt(encodable.answer().ordinal()),
            ctx -> new ClientboundHandshakePacket(Answer.values()[ctx.buffer().readInt()])
    );

    @Override
    public PacketType<? extends PacketWithoutResponse> type() {
        return null;
    }

    public enum Answer {
        SUCCESS,
        WRONG_PROTOCOL,
        WRONG_HASH
    }

}
