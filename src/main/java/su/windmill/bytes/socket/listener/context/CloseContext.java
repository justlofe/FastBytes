package su.windmill.bytes.socket.listener.context;

import su.windmill.bytes.socket.WebSocket;

import java.util.Optional;

/**
 * Executed when connection with server is closed
 */
public class CloseContext extends WebSocketContext {

    private final int code;
    private final Optional<String> reason;

    public CloseContext(WebSocket socket, int code, String reason) {
        super(socket);
        this.code = code;
        this.reason = Optional.ofNullable(reason);
    }

    public int code() {
        return code;
    }

    public Optional<String> reason() {
        return reason;
    }

}
