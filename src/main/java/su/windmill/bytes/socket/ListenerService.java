package su.windmill.bytes.socket;

import su.windmill.bytes.socket.exception.ListenerCallException;
import su.windmill.bytes.socket.listener.Listener;
import su.windmill.bytes.socket.listener.context.ContextType;
import su.windmill.bytes.socket.listener.context.ListenerContext;
import su.windmill.bytes.util.Assertions;
import su.windmill.bytes.util.Key;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for handling listeners both on client and server implementation
 */
public final class ListenerService {

    private final Map<String, Map<Key, Listener<?>>> LISTENERS = new HashMap<>();

    public <C extends ListenerContext, L extends Listener<C>> void addListener(Key key, ContextType<C> type, L listener) {
        Assertions.notNull(key, "key");
        Assertions.notNull(type, "type");
        Assertions.notNull(listener, "listener");
        listeners(type).put(key, listener);
    }

    public <C extends ListenerContext> void call(ContextType<C> type, C context) {
        Assertions.notNull(type, "type");
        Assertions.notNull(context, "context");

        Map<Key, Listener<?>> listeners = listeners(type);
        for (Key key : listeners.keySet()) {
            Listener<?> raw = listeners.get(key);
            if(raw == null) throw new RuntimeException("for some reason listener is null");
            Listener<C> listener = (Listener<C>) raw;
            try {
                listener.call(context);
            }
            catch (Throwable throwable) {
                new ListenerCallException(key, type, throwable).printStackTrace();
            }
        }
    }

    public void removeListener(Key key, ContextType<?> type) {
        Assertions.notNull(key, "key");
        Assertions.notNull(type, "type");

        Map<Key, Listener<?>> listeners = LISTENERS.get(type.id());
        if(listeners == null) return;

        listeners.remove(key);
    }

    private <C extends ListenerContext> Map<Key, Listener<?>> listeners(ContextType<C> type) {
        return LISTENERS.computeIfAbsent(
                type.id(),
                (_) -> new HashMap<>()
        );
    }

}
