package su.windmill.bytes.socket.connection;

import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.util.Either;

@FunctionalInterface
public interface MessageConsumer {

    void accept(WebSocketConnection connection, Either<FastBuffer, String> message);

}
