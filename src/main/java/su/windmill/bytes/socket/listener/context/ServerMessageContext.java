package su.windmill.bytes.socket.listener.context;

import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.socket.WebSocket;
import su.windmill.bytes.socket.server.WebSocketServer;

public final class ServerMessageContext extends MessageContext {

    private final WebSocket client;

    public ServerMessageContext(WebSocketServer socket, FastBuffer message, boolean textMessage, WebSocket client) {
        super(socket, message, textMessage);
        this.client = client;
    }

    @Override
    public WebSocketServer socket() {
        return (WebSocketServer) super.socket();
    }

    public WebSocket client() {
        return client;
    }

}
