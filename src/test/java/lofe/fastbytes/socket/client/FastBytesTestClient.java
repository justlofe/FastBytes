package lofe.fastbytes.socket.client;

import org.junit.jupiter.api.Assertions;
import lofe.fastbytes.buffer.FastBuffer;
import lofe.fastbytes.util.SimpleLogger;

import java.net.URI;

final class FastBytesTestClient extends AbstractWebSocketClient implements TestClient, ClientHandler {

    private final SimpleLogger logger = new SimpleLogger(this);

    private final String expectedResponse;

    public FastBytesTestClient(URI uri, String expectedResponse) {
        super(uri);
        clientHandler(this);

        this.expectedResponse = expectedResponse;
    }

    @Override
    public void open(WebSocketClient client) {
        logger.info("Connected");
    }

    @Override
    public void close(WebSocketClient client, int code, String reason) {
        logger.info(String.format(
                "Closed connection. [Code: %s, Reason: \"%s\"]",
                code,
                reason
        ));
    }

    @Override
    public void message(WebSocketClient client, FastBuffer buffer, String textMessage) {
        String message;
        if(buffer == null) message = textMessage;
        else message = "binary";

        logger.info("Received message from server: [Message: \"" + message + "\"]");

        Assertions.assertEquals(expectedResponse, message);
        logger.warn("Test passed!");
        try {
            testClose();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void error(WebSocketClient client, Throwable throwable) {
        logger.warn("Exception thrown: " + throwable.getMessage());
    }

    @Override
    public void testConnect() throws Exception {
        connect();
    }

    @Override
    public void testClose() {
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
