package su.windmill.bytes.codec;

import su.windmill.bytes.buffer.FastBuffer;

@FunctionalInterface
public interface Encoder<E> {

    void encode(E encodable, FastBuffer buffer);

}
