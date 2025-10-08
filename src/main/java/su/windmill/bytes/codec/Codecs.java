package su.windmill.bytes.codec;

import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.buffer.FixedBuffer;

import java.util.UUID;

public class Codecs {

    public static final Codec<UUID> UUID = Codec.fixed(
            (encodable, buffer) -> {
                buffer.writeLong(encodable.getMostSignificantBits());
                buffer.writeLong(encodable.getLeastSignificantBits());
            },
            ctx -> {
                FastBuffer buffer = ctx.buffer();
                return new UUID(buffer.readLong(), buffer.readLong());
            }
    );

    private Codecs() {
        throw new UnsupportedOperationException();
    }

}
