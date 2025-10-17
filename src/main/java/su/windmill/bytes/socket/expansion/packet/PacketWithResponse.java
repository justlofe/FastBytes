package su.windmill.bytes.socket.expansion.packet;

/**
 * Packet, after which WebSocket would start wait for the response to be sent
 * @param <R> response packet type
 */
public interface PacketWithResponse<R extends PacketWithoutResponse> extends Packet {

    PacketType<R> responseType();

}
