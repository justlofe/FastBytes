package su.windmill.bytes.socket.client;

import org.junit.jupiter.api.Assertions;
import su.windmill.bytes.socket.listener.context.ContextType;
import su.windmill.bytes.util.Key;
import su.windmill.bytes.util.SimpleLogger;

import java.net.URI;

public class FastBytesTestClient extends AbstractWebSocketClient implements TestClient {

    private final SimpleLogger logger = new SimpleLogger(this);

    private final String expectedResponse;

    public FastBytesTestClient(URI uri, String expectedResponse) {
        super(uri);
        this.expectedResponse = expectedResponse;
    }

    @Override
    public void testConnect() throws Exception {
        addListener(Key.key("open"), ContextType.OPEN, _ -> logger.info("Connected"));
        addListener(Key.key("close"), ContextType.CLOSE, context -> {
            logger.info(String.format(
                    "Closed connection. [Code: %s, Reason: \"%s\"]",
                    context.code(),
                    context.reason().orElse("no reason")
            ));
        });
        addListener(Key.key("message"), ContextType.MESSAGE, context -> {
            String message = context.textMessage().orElse("binary");
            logger.info("Received message from server: [Message: \"" + message + "\"]");

            Assertions.assertEquals(expectedResponse, message);
            logger.warn("Test passed!");
            try {
                testClose();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        addListener(Key.key("error"), ContextType.ERROR, context -> {
            logger.warn("Exception thrown: " + context.throwable().getMessage());
        });

        connect();
    }

    @Override
    public void testClose() throws Exception {
        close();
    }

    @Override
    public void testSend(String message) {
        sendText(message);
    }

    @Override
    public boolean testActive() {
        return isConnected();
    }

}
