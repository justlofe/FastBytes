package su.windmill.bytes.socket.connection;

import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.util.Either;

import java.util.function.Consumer;

@FunctionalInterface
public interface MessageConsumer extends Consumer<Either<FastBuffer, String>> {
}
