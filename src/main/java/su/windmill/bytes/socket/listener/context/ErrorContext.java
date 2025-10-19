package su.windmill.bytes.socket.listener.context;

import su.windmill.bytes.socket.WebSocket;

/**
 * Executed when some error thrown while executing some websocket logic
 */
public final class ErrorContext extends WebSocketContext {

    private final Throwable throwable;

    public ErrorContext(WebSocket socket, Throwable throwable) {
        super(socket);
        this.throwable = throwable;
    }

    public Throwable throwable() {
        return throwable;
    }
}
