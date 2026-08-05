package lofe.fastbytes.codec;

import lofe.fastbytes.buffer.FastBuffer;

import java.util.UUID;

public class Codecs {

    public static final StreamCodec<UUID> UUID = StreamCodec.fixed(
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
