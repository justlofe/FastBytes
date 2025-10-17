package su.windmill.bytes.socket.expansion.client;

import su.windmill.bytes.socket.WebSocket;
import su.windmill.bytes.socket.expansion.packet.PacketWithoutResponse;

/**
 * Listens for specific packet
 * @param <P> packet type
 */
@FunctionalInterface
public interface PacketWithoutResponseListener<P extends PacketWithoutResponse> {

    void call(WebSocket socket, P packet);

}
