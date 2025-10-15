package su.windmill.bytes.socket.expansion.handshake;

import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.codec.Codec;
import su.windmill.bytes.socket.expansion.packet.PacketRegistry;
import su.windmill.bytes.socket.expansion.packet.PacketType;
import su.windmill.bytes.socket.expansion.packet.PacketWithResponse;

public record ServerboundHandshakePacket(String protocolName, long hash) implements PacketWithResponse<ClientboundHandshakePacket> {

    public static final Codec<ServerboundHandshakePacket> CODEC = Codec.fixed(
            (encodable, buffer) -> {
                buffer.writeUTF8(encodable.protocolName());
                buffer.writeLong(buffer.readLong());
            },
            ctx -> {
                FastBuffer buffer = ctx.buffer();
                return new ServerboundHandshakePacket(buffer.readUTF8(), buffer.readLong());
            }
    );

    @Override
    public PacketType<ServerboundHandshakePacket> type() {
        return PacketRegistry.SERVERBOUND_HANDSHAKE;
    }

    @Override
    public PacketType<ClientboundHandshakePacket> responseType() {
        return PacketRegistry.CLIENTBOUND_HANDSHAKE;
    }

}
