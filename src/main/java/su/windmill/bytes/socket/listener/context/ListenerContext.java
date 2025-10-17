package su.windmill.bytes.socket.listener.context;

import su.windmill.bytes.socket.WebSocket;

/**
 * Context for listener (event info in other words)
 */
public interface ListenerContext {

    /**
     * WebSocket where this event happened
     */
    WebSocket socket();

}
