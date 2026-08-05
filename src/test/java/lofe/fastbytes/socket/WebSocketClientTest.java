package lofe.fastbytes.socket;

import org.junit.jupiter.api.Test;
import lofe.fastbytes.socket.client.TestClient;
import lofe.fastbytes.socket.server.TestServer;

import java.net.URI;

public class WebSocketClientTest {

    @Test
    public void test() throws Exception {
        long start = System.currentTimeMillis();
        TestServer server = TestServer.thirdParty("Ping!", "Pong!");
        server.testStart();

        TestClient client = TestClient.fastBytes(URI.create("http://localhost:" + server.port()), "Pong!");
        client.testConnect();

        System.out.println("Start elapsed time " + (System.currentTimeMillis() - start) + "ms");

        client.testSend("Ping!");

        while (client.testActive());
        System.out.println("Test elapsed time " + (System.currentTimeMillis() - start) + "ms");
    }

}
