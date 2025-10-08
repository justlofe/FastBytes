package su.windmill.codecs;

import su.windmill.buffer.FastBuffer;
import su.windmill.codec.Codec;
import su.windmill.codec.Decoder;
import su.windmill.codec.Encoder;
import su.windmill.codec.context.DecodeContext;

public class FixedCodec<E> implements Codec<E> {

    private final Encoder<E> encoder;
    private final Decoder<E> decoder;

    public FixedCodec(Encoder<E> encoder, Decoder<E> decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    @Override
    public void encode(E encodable, FastBuffer buffer) {
        encoder.encode(encodable, buffer);
    }

    @Override
    public E decode(DecodeContext ctx) {
        return decoder.decode(ctx);
    }

}
