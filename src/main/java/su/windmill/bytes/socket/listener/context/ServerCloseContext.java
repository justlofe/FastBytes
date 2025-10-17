package su.windmill.bytes.socket.listener.context;

import su.windmill.bytes.socket.WebSocket;
import su.windmill.bytes.socket.connection.WebSocketConnection;

/**
 * Executed when connection with client is closed on server
 */
public final class ServerCloseContext extends CloseContext {

    private final WebSocketConnection client;

    public ServerCloseContext(WebSocket socket, int code, String reason, WebSocketConnection client) {
        super(socket, code, reason);
        this.client = client;
    }

    public WebSocketConnection client() {
        return client;
    }

}
