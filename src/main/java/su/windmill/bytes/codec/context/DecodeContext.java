package su.windmill.bytes.codec.context;

import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.codec.Decoder;

import java.util.logging.Logger;

public interface DecodeContext {

    FastBuffer buffer();

    default Logger logger() {
        return Logger.getLogger(this.getClass().getSimpleName());
    }

    default <E> E decode(Decoder<E> decoder) {
        return decoder.decode(this);
    }

    static DecodeContext of(FastBuffer buffer) {
        return new SimpleDecodeContext(buffer);
    }

}
