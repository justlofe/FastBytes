package su.windmill.codec;

import su.windmill.buffer.FastBuffer;

@FunctionalInterface
public interface Encoder<E> {

    void encode(E encodable, FastBuffer buffer);

}
