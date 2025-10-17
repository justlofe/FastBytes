package su.windmill.bytes.socket;

import su.windmill.bytes.util.Key;
import su.windmill.bytes.socket.listener.Listener;
import su.windmill.bytes.socket.listener.context.ContextType;
import su.windmill.bytes.socket.listener.context.ListenerContext;

/**
 * Shared interface to client and server
 */
public interface WebSocket {

    int
            CLOSE_NORMAL = 1000,
            CLOSE_GOING_AWAY = 1001,
            CLOSE_PROTOCOL_ERROR = 1002,
            REFUSE = 1003;

    <C extends ListenerContext, L extends Listener<C>> void addListener(Key key, ContextType<C> type, L listener);

    void removeListener(Key key, ContextType<?> type);

}
