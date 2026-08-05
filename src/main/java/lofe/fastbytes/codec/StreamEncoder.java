package lofe.fastbytes.codec;

import lofe.fastbytes.buffer.FastBuffer;

@FunctionalInterface
public interface StreamEncoder<E> {

    void encode(E encodable, FastBuffer buffer);

}
