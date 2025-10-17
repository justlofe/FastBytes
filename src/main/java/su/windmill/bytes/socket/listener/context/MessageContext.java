package su.windmill.bytes.socket.listener.context;

import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.socket.WebSocket;

import java.util.Optional;

/**
 * Executed when client received a message from server
 */
public sealed class MessageContext extends WebSocketContext permits ServerMessageContext {

    private final FastBuffer message;
    private final Optional<String> textMessage;

    public MessageContext(WebSocket socket, FastBuffer message, Optional<String> textMessage) {
        super(socket);
        this.message = message;
        this.textMessage = textMessage;
    }

    public FastBuffer message() {
        return message;
    }

    /**
     * Returns message as text. Returns empty, if message is presented not as text.
     */
    public Optional<String> textMessage() {
        return textMessage;
    }

}
