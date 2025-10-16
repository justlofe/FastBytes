package su.windmill.bytes.socket.listener.context;

import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.socket.connection.WebSocketConnection;
import su.windmill.bytes.socket.server.WebSocketServer;

import java.util.Optional;

public final class ServerMessageContext extends MessageContext {

    private final WebSocketConnection client;

    public ServerMessageContext(WebSocketServer socket, FastBuffer message, Optional<String> textMessage, WebSocketConnection client) {
        super(socket, message, textMessage);
        this.client = client;
    }

    @Override
    public WebSocketServer socket() {
        return (WebSocketServer) super.socket();
    }

    public WebSocketConnection client() {
        return client;
    }

}
