package su.windmill.bytes.socket.listener.context;

import su.windmill.bytes.socket.WebSocket;
import su.windmill.bytes.socket.connection.WebSocketConnection;

/**
 * Executed when new client connected to server
 */
public final class ServerOpenContext extends WebSocketContext {

    private final WebSocketConnection connection;

    public ServerOpenContext(WebSocket socket, WebSocketConnection connection) {
        super(socket);
        this.connection = connection;
    }

    public WebSocketConnection connection() {
        return connection;
    }

}
