package su.windmill.bytes;

import org.junit.jupiter.api.Test;
import su.windmill.bytes.socket.expansion.handshake.ClientboundHandshakePacket;
import su.windmill.bytes.socket.expansion.handshake.ServerboundHandshakePacket;
import su.windmill.bytes.socket.expansion.client.PacketWebSocketClient;

import java.io.IOException;
import java.net.URI;

public class PacketWebSocketClientTest {

    @Test
    public void test() throws IOException {
        PacketWebSocketClient client = new PacketWebSocketClient(URI.create("ws://localhost:8080"));
        client.connect();

        client.sendWithResponse(new ServerboundHandshakePacket(
                "your-cool-protocol-name", 1L
        )).thenAccept(handshake -> {
            if(handshake.answer() != ClientboundHandshakePacket.Answer.SUCCESS) {
                client.close();
                return;
            }

            System.out.println("Established");
        });
    }

}
