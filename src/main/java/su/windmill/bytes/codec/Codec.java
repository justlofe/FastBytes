package su.windmill.bytes.codec;

import su.windmill.bytes.codecs.FixedCodec;

public interface Codec<E> extends Encoder<E>, Decoder<E> {

    static <E> Codec<E> fixed(Encoder<E> encoder, Decoder<E> decoder) {
        return new FixedCodec<>(encoder, decoder);
    }

}
