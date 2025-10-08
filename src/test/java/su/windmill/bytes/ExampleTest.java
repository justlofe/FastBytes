package su.windmill.bytes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.codec.Codec;
import su.windmill.bytes.codec.context.DecodeContext;

import java.util.UUID;

public class ExampleTest {

    public static final Codec<ExampleEncodable> ENCODABLE_CODEC = Codec.fixed(
            (encodable, buffer) -> {
                buffer.writeLong(encodable.id.getMostSignificantBits());
                buffer.writeLong(encodable.id.getLeastSignificantBits());

                buffer.writeUTF8(encodable.name);

                buffer.writeInt(encodable.someParameter);
            },
            (ctx) -> {
                FastBuffer buffer = ctx.buffer();
                return new ExampleEncodable(
                        new UUID(
                                buffer.readLong(),
                                buffer.readLong()
                        ),
                        buffer.readUTF8(),
                        buffer.readInt()
                );
            }
    );

    @Test
    public void test() {
        ExampleEncodable exampleEncodable = ExampleEncodable.randomized();
        FastBuffer buffer = FastBytes.expanding();

        ENCODABLE_CODEC.encode(exampleEncodable, buffer);

        ExampleEncodable decoded = ENCODABLE_CODEC.decode(DecodeContext.of(buffer));

        Assertions.assertEquals(exampleEncodable.id, decoded.id);
        Assertions.assertEquals(exampleEncodable.name, decoded.name);
        Assertions.assertEquals(exampleEncodable.someParameter, decoded.someParameter);
    }

}