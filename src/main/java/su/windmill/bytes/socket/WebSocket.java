package su.windmill.bytes.socket;

import su.windmill.bytes.socket.listener.Listener;
import su.windmill.bytes.socket.listener.context.ContextType;
import su.windmill.bytes.socket.listener.context.ListenerContext;
import su.windmill.bytes.util.Key;

/**
 * Shared interface to client and server
 */
public interface WebSocket {

     int
            CLOSE_NORMAL = 1000,
            CLOSE_GOING_AWAY = 1001,
            CLOSE_PROTOCOL_ERROR = 1002,
            REFUSE = 1003;

    /**
     * Adds a listener on specific context
     * @param key unique id of listener
     * @param type context type to listen to
     * @param listener listener itself
     */
    <C extends ListenerContext, L extends Listener<C>> void addListener(Key key, ContextType<C> type, L listener);

    /**
     * Removes listener from specific context
     * @param key unique id of listener
     * @param type context from which remove listener
     */
    void removeListener(Key key, ContextType<?> type);

}
