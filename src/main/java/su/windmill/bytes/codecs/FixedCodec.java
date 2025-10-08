package su.windmill.bytes.codecs;

import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.codec.Codec;
import su.windmill.bytes.codec.Decoder;
import su.windmill.bytes.codec.Encoder;
import su.windmill.bytes.codec.context.DecodeContext;

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
