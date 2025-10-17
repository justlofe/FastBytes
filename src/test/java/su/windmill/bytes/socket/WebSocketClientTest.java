package su.windmill.bytes.socket;

import org.junit.jupiter.api.Test;
import su.windmill.bytes.socket.client.TestClient;
import su.windmill.bytes.socket.server.TestServer;

import java.net.URI;

public class WebSocketClientTest {

    @Test
    public void test() throws Exception {
        TestServer server = TestServer.thirdParty("Ping!", "Pong!");
        server.testStart();

        TestClient client = TestClient.fastBytes(URI.create("http://localhost:" + server.port()), "Pong!");
        client.testConnect();

        client.testSend("Ping!");

        while (client.testActive());
    }

}
