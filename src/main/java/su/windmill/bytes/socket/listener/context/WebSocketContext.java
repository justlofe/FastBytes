package su.windmill.bytes.socket.listener.context;

import su.windmill.bytes.socket.WebSocket;

public class WebSocketContext implements ListenerContext {

    private final WebSocket socket;

    public WebSocketContext(WebSocket socket) {
        this.socket = socket;
    }

    @Override
    public WebSocket socket() {
        return socket;
    }

}
