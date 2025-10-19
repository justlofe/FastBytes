package su.windmill.bytes.socket.connection;

@FunctionalInterface
public interface ErrorConsumer {

    void accept(WebSocketConnection connection, Throwable throwable);

}
