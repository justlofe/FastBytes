package lofe.fastbytes.socket.connection;

import lofe.fastbytes.buffer.FastBuffer;
import lofe.fastbytes.util.Either;

@FunctionalInterface
public interface MessageConsumer {

    void accept(WebSocketConnection connection, Either<FastBuffer, String> message);

}
