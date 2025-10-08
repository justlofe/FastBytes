package su.windmill.codec;

import su.windmill.codecs.FixedCodec;

public interface Codec<E> extends Encoder<E>, Decoder<E> {

    static <E> Codec<E> fixed(Encoder<E> encoder, Decoder<E> decoder) {
        return new FixedCodec<>(encoder, decoder);
    }

}
