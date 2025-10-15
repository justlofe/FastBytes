package su.windmill.bytes.socket.listener.context;

import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.socket.WebSocket;

import java.util.Optional;

public sealed class MessageContext extends WebSocketContext permits ServerMessageContext {

    private final FastBuffer message;
    private final boolean textMessage;

    private String readTextMessage;

    public MessageContext(WebSocket socket, FastBuffer message, boolean textMessage) {
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
        if(!textMessage) return Optional.empty();
        return Optional.ofNullable(readTextMessage)
                .or(() -> Optional.ofNullable(readTextMessage = message.readUTF8()));
    }

}
