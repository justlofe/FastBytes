package su.windmill.bytes.socket;

import su.windmill.bytes.buffer.FastBuffer;

import java.util.function.Consumer;

/**
 * Interface for writing binary messages
 */
@FunctionalInterface
public interface MessageWriter extends Consumer<FastBuffer> {

}
