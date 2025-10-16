package su.windmill.bytes.socket.listener.context;

public final class ContextType<C extends ListenerContext> {

    public static final ContextType<WebSocketContext> OPEN = new ContextType<>("open", WebSocketContext.class);
    public static final ContextType<MessageContext> MESSAGE = new ContextType<>("message", MessageContext.class);
    public static final ContextType<ServerMessageContext> SERVER_MESSAGE = new ContextType<>("server_message", ServerMessageContext.class);
    public static final ContextType<CloseContext> CLOSE = new ContextType<>("close", CloseContext.class);
    public static final ContextType<ServerCloseContext> SERVER_CLOSE = new ContextType<>("server_close", ServerCloseContext.class);
    public static final ContextType<ErrorContext> ERROR = new ContextType<>("error", ErrorContext.class);

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
