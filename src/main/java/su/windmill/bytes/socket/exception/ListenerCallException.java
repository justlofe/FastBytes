package su.windmill.bytes.socket.exception;

import su.windmill.bytes.util.Key;
import su.windmill.bytes.socket.listener.context.ContextType;

/**
 * Thrown when call to listener result to an exception
 */
public class ListenerCallException extends Exception {

    public ListenerCallException(Key key, ContextType<?> type, Throwable throwable) {
        super(String.format(
                "Listener on %s type with \"%s\" thrown an exception on call",
                type.id(),
                key.value()
        ), throwable);
    }

}
