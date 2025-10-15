package su.windmill.bytes;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import su.windmill.bytes.socket.client.AbstractWebSocketClient;
import su.windmill.bytes.socket.listener.context.ContextType;
import su.windmill.bytes.util.Key;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebSocketClientTest {

    @Test
    public void test() throws IOException {
        ThirdPartyTestServer testServer = prepareTestServer();

        URI uri = URI.create("ws://localhost:8080");
        TestClient client = new TestClient(uri);

        client.addListener(
                Key.key("open"),
                ContextType.OPEN,
                _ -> System.out.println("[Client] Opened!")
        );

        client.addListener(
                Key.key("close"),
                ContextType.CLOSE,
                context -> System.out.printf(
                        "[Client] Closed! %s: [%s]%n\n",
                        context.code(),
                        context.reason().orElse("")
                )
        );

        client.addListener(
                Key.key("error"),
                ContextType.ERROR,
                context -> System.out.println("[Client] Error: " + context.throwable().getMessage())
        );

        client.addListener(
                Key.key("message"),
                ContextType.MESSAGE,
                context -> {
                    Optional<String> textMessage = context.textMessage();
                    Assertions.assertEquals("Pong!", textMessage.orElse(null));
                    System.out.println("[Client] Test passed!");
                    client.close();
                    try {
                        testServer.stop();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        client.connect();

        System.out.println("[Client] Sending test message..");
        client.sendText("Ping!");
    }

    private ThirdPartyTestServer prepareTestServer() {
        ThirdPartyTestServer testServer = new ThirdPartyTestServer(new InetSocketAddress(8080));
        testServer.start();
        return testServer;
    }

    private static final class TestClient extends AbstractWebSocketClient {
        public TestClient(URI uri) {
            super(uri);
        }
    }

    private static final class ThirdPartyTestServer extends WebSocketServer {

        private final ExecutorService executorService = Executors.newSingleThreadExecutor();
        
        public ThirdPartyTestServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            log("Connection created");
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            log(String.format(
                    "Connection closed: %s [%s]\n",
                    code,
                    reason
            ));
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            if("Ping!".equalsIgnoreCase(message)) {
                log("Got a test message. Answering with answer client waiting for.");
                executorService.execute(() -> conn.send("Pong!"));
            }
            else log("Got a message, but it doesn't equals test message.");
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {

        }

        @Override
        public void onStart() {
            log("Started");
        }

        private static void log(String message) {
            System.out.printf(
                    "[%s] %s\n",
                    ThirdPartyTestServer.class.getSimpleName(),
                    message
            );
        }
        
    }

}
