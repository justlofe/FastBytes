package su.windmill.bytes.socket.expansion.client;

import su.windmill.bytes.socket.expansion.packet.PacketWithResponse;
import su.windmill.bytes.socket.expansion.packet.PacketWithoutResponse;

import java.util.concurrent.CompletableFuture;

/**
 * WebSocket packet client expansion interface
 */
public interface PacketClient {

    <P extends PacketWithoutResponse> void sendWithoutResponse(P packet);

    <R extends PacketWithoutResponse, P extends PacketWithResponse<R>> CompletableFuture<R> sendWithResponse(P packet);

}
