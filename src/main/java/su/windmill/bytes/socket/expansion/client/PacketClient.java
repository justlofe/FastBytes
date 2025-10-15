package su.windmill.bytes.socket.expansion.client;

import su.windmill.bytes.socket.expansion.packet.PacketWithoutResponse;
import su.windmill.bytes.socket.expansion.packet.PacketWithResponse;

import java.util.concurrent.CompletableFuture;

public interface PacketClient {

    <P extends PacketWithoutResponse> void sendWithoutResponse(P packet);

    <R extends PacketWithoutResponse, P extends PacketWithResponse<R>> CompletableFuture<R> sendWithResponse(P packet);

}
