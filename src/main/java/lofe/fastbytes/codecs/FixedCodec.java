package lofe.fastbytes.codecs;

import lofe.fastbytes.buffer.FastBuffer;
import lofe.fastbytes.codec.StreamCodec;
import lofe.fastbytes.codec.StreamDecoder;
import lofe.fastbytes.codec.StreamEncoder;
import lofe.fastbytes.codec.context.DecodeContext;

public class FixedCodec<E> implements StreamCodec<E> {

    private final StreamEncoder<E> streamEncoder;
    private final StreamDecoder<E> streamDecoder;

    public FixedCodec(StreamEncoder<E> streamEncoder, StreamDecoder<E> streamDecoder) {
        this.streamEncoder = streamEncoder;
        this.streamDecoder = streamDecoder;
    }

    @Override
    public void encode(E encodable, FastBuffer buffer) {
        streamEncoder.encode(encodable, buffer);
    }

    @Override
    public E decode(DecodeContext ctx) {
        return streamDecoder.decode(ctx);
    }

}
