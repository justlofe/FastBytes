package su.windmill.bytes.socket.expansion.client;

import su.windmill.bytes.socket.WebSocket;
import su.windmill.bytes.socket.expansion.packet.PacketWithResponse;
import su.windmill.bytes.socket.expansion.packet.PacketWithoutResponse;

@FunctionalInterface
public interface PacketWithResponseListener<R extends PacketWithoutResponse, P extends PacketWithResponse<R>> {

    R call(WebSocket socket, P packet);

}
