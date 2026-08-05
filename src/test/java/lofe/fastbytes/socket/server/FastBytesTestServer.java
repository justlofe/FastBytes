package lofe.fastbytes.socket.server;

import lofe.fastbytes.buffer.FastBuffer;
import lofe.fastbytes.socket.connection.WebSocketConnection;
import lofe.fastbytes.util.SimpleLogger;

import java.net.InetSocketAddress;

final class FastBytesTestServer extends AbstractWebSocketServer implements TestServer, ServerHandler {

    private final SimpleLogger logger = new SimpleLogger(this);

    private final int port;
    private final String message, response;

    public FastBytesTestServer(int port, String message, String response) {
        super(new InetSocketAddress(port));
        this.port = port;
        this.message = message;
        this.response = response;

        serverHandler(this);
    }

    @Override
    public void open(WebSocketServer server, WebSocketConnection client) {
        logger.info("Connection opened");
    }

    @Override
    public void close(WebSocketServer server, WebSocketConnection client, int code, String reason) {
        logger.info(String.format(
                "Closed connection. [Code: %s, Reason: \"%s\"]",
                code,
                reason
        ));
    }

    @Override
    public void message(WebSocketServer server, WebSocketConnection client, FastBuffer buffer, String textMessage) {
        String message;
        if(buffer == null) message = textMessage;
        else message = "binary";

        logger.info("Got a message from client: [Message: \"" + message + "\"]");
        if(this.message.equals(message)) {
            sendText(client, this.response);
        }
    }

    @Override
    protected void error(WebSocketConnection client, Throwable throwable) {
        logger.warn("Exception thrown: " + throwable.getMessage());
    }

    @Override
    public void start(WebSocketServer server) {
        logger.info("Started");
    }

    @Override
    public void testStart() throws Exception {
        start();
    }

    @Override
    public void testStop() throws Exception {
        stop();
    }

    @Override
    public int port() {
        return port;
    }

}
