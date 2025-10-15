package su.windmill.bytes.socket.expansion.client;

import su.windmill.bytes.socket.WebSocket;
import su.windmill.bytes.socket.expansion.packet.PacketWithoutResponse;

@FunctionalInterface
public interface PacketWithoutResponseListener<P extends PacketWithoutResponse> {

    void call(WebSocket socket, P packet);

}
