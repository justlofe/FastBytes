package su.windmill.bytes.socket.listener;

import su.windmill.bytes.socket.listener.context.ListenerContext;

public interface Listener<C extends ListenerContext> {

    void call(C context);

}
