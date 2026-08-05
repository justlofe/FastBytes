package lofe.fastbytes.codec.context;

import lofe.fastbytes.buffer.FastBuffer;
import lofe.fastbytes.codec.StreamDecoder;

import java.util.logging.Logger;

public interface DecodeContext {

    FastBuffer buffer();

    default Logger logger() {
        return Logger.getLogger(this.getClass().getSimpleName());
    }

    default <E> E decode(StreamDecoder<E> streamDecoder) {
        return streamDecoder.decode(this);
    }

    static DecodeContext of(FastBuffer buffer) {
        return new SimpleDecodeContext(buffer);
    }

}
