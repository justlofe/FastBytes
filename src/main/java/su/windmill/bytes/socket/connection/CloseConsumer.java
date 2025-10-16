package su.windmill.bytes.socket.connection;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface CloseConsumer extends BiConsumer<Integer, String> {
}
