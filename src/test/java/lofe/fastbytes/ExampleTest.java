package lofe.fastbytes;

import lofe.fastbytes.codec.StreamCodec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import lofe.fastbytes.buffer.FastBuffer;
import lofe.fastbytes.codec.context.DecodeContext;

import java.util.UUID;

public class ExampleTest {

    public static final StreamCodec<ExampleEncodable> ENCODABLE_STREAM_CODEC = StreamCodec.fixed(
            (encodable, buffer) -> {
                buffer.writeLong(encodable.id.getMostSignificantBits());
                buffer.writeLong(encodable.id.getLeastSignificantBits());

                buffer.writeUTF8(encodable.name);

                buffer.writeVarInt(encodable.someParameter);
            },
            (ctx) -> {
                FastBuffer buffer = ctx.buffer();
                return new ExampleEncodable(
                        new UUID(
                                buffer.readLong(),
                                buffer.readLong()
                        ),
                        buffer.readUTF8(),
                        buffer.readVarInt()
                );
            }
    );

    @Test
    public void test() {
        ExampleEncodable exampleEncodable = ExampleEncodable.randomized();
        try (FastBuffer buffer = FastBytes.expanding()) {
            ENCODABLE_STREAM_CODEC.encode(exampleEncodable, buffer);

            ExampleEncodable decoded = ENCODABLE_STREAM_CODEC.decode(DecodeContext.of(buffer));

            Assertions.assertEquals(exampleEncodable.id, decoded.id);
            Assertions.assertEquals(exampleEncodable.name, decoded.name);
            Assertions.assertEquals(exampleEncodable.someParameter, decoded.someParameter);
        }
    }

}