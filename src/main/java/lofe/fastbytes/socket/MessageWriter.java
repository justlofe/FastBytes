package lofe.fastbytes.socket;

import lofe.fastbytes.buffer.FastBuffer;

import java.util.function.Consumer;

/**
 * Interface for writing binary messages
 */
@FunctionalInterface
public interface MessageWriter extends Consumer<FastBuffer> {

}
