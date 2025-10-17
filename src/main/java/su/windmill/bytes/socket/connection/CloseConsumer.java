package su.windmill.bytes.socket.connection;

@FunctionalInterface
public interface CloseConsumer {

    void accept(WebSocketConnection connection, int code, String reason);

}
