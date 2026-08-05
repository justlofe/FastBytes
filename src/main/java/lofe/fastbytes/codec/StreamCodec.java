package lofe.fastbytes.codec;

import lofe.fastbytes.codecs.CollectionCodec;
import lofe.fastbytes.codecs.FixedCodec;
import lofe.fastbytes.codecs.MapCodec;

import java.util.function.Function;

public interface StreamCodec<E> extends StreamEncoder<E>, StreamDecoder<E> {

    static <E> StreamCodec<E> fixed(StreamEncoder<E> encoder, StreamDecoder<E> decoder) {
        return new FixedCodec<>(encoder, decoder);
    }

    static <K, V> MapCodec<K, V> map(StreamCodec<K> keyCodec, StreamCodec<V> valueCodec) {
        return new MapCodec<>(keyCodec, valueCodec);
    }

    static <E> CollectionCodec<E> stream(StreamCodec<E> streamCodec) {
        return new CollectionCodec<>(streamCodec);
    }

}
