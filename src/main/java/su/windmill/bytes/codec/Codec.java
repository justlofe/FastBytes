package su.windmill.bytes.codec;

import su.windmill.bytes.codecs.FixedCodec;
import su.windmill.bytes.codecs.MapCodec;
import su.windmill.bytes.codecs.StreamCodec;

public interface Codec<E> extends Encoder<E>, Decoder<E> {

    static <E> Codec<E> fixed(Encoder<E> encoder, Decoder<E> decoder) {
        return new FixedCodec<>(encoder, decoder);
    }

    static <K, V> MapCodec<K, V> map(Codec<K> keyCodec, Codec<V> valueCodec) {
        return new MapCodec<>(keyCodec, valueCodec);
    }

    static <E> StreamCodec<E> stream(Codec<E> codec) {
        return new StreamCodec<>(codec);
    }

}
