package su.windmill.bytes.socket.connection;

import java.util.function.Consumer;

@FunctionalInterface
public interface ErrorConsumer extends Consumer<Throwable> {
}
