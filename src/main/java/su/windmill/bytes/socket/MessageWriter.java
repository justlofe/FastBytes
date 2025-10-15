package su.windmill.bytes.socket;

import su.windmill.bytes.buffer.FastBuffer;

import java.util.function.Consumer;

@FunctionalInterface
public interface MessageWriter extends Consumer<FastBuffer> {

}
