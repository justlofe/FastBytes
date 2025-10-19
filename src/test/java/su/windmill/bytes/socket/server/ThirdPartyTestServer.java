package su.windmill.bytes.socket.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import su.windmill.bytes.util.SimpleLogger;

import java.net.InetSocketAddress;

class ThirdPartyTestServer extends WebSocketServer implements TestServer {

    private final SimpleLogger logger = new SimpleLogger(this);

    private final int port;
    private final String message, response;

    public ThirdPartyTestServer(int port, String message, String response) {
        super(new InetSocketAddress(port));
        this.port = port;
        this.message = message;
        this.response = response;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info("Connection opened");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.info(String.format(
                "Closed connection. [Code: %s, Reason: \"%s\", Remote: %s]",
                code,
                reason,
                remote
        ));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        logger.info("Got a message from client: [Message: \"" + message + "\"]");
        if(this.message.equals(message)) {
            conn.send(this.response);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.warn("Exception thrown: " + ex.getMessage());
    }

    @Override
    public void onStart() {
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
