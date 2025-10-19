package su.windmill.bytes.socket.listener;

import su.windmill.bytes.socket.listener.context.ListenerContext;

/**
 * Listener for specific context
 * @param <C> context type
 */
public interface Listener<C extends ListenerContext> {

    /**
     * Called when some event happens
     * @param context context of event happened
     */
    void call(C context);

}
