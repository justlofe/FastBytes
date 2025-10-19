package su.windmill.bytes.socket.listener.context;

/**
 * ContextType used for registering Listener's
 * @param <C>
 */
public final class ContextType<C extends ListenerContext> {

    // both
    public static final ContextType<ErrorContext> ERROR = new ContextType<>("error", ErrorContext.class);

    // client
    public static final ContextType<WebSocketContext> OPEN = new ContextType<>("open", WebSocketContext.class);
    public static final ContextType<MessageContext> MESSAGE = new ContextType<>("message", MessageContext.class);
    public static final ContextType<CloseContext> CLOSE = new ContextType<>("close", CloseContext.class);

    // server
    public static final ContextType<WebSocketContext> START = new ContextType<>("server_start", WebSocketContext.class);
    public static final ContextType<ServerOpenContext> SERVER_OPEN = new ContextType<>("server_open", ServerOpenContext.class);
    public static final ContextType<ServerMessageContext> SERVER_MESSAGE = new ContextType<>("server_message", ServerMessageContext.class);
    public static final ContextType<ServerCloseContext> SERVER_CLOSE = new ContextType<>("server_close", ServerCloseContext.class);

    private final String id;
    private final Class<C> clazz;

    public ContextType(String id, Class<C> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public String id() {
        return id;
    }

    public Class<C> contextClass() {
        return clazz;
    }

}
