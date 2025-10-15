package su.windmill.bytes.socket.expansion.packet;

public interface PacketWithResponse<R extends PacketWithoutResponse> extends Packet {

    PacketType<R> responseType();

}
