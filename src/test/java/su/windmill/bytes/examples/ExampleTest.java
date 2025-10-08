package su.windmill.bytes.examples;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import su.windmill.bytes.FastBytes;
import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.codec.Codec;
import su.windmill.bytes.codec.context.DecodeContext;

import java.util.UUID;

public class ExampleTest {

    public static final Codec<ExampleEncodable> EXAMPLE_CODEC = Codec.fixed(
            (encodable, buffer) -> {
                // Encoding id
                buffer.writeLong(encodable.id.getMostSignificantBits());
                buffer.writeLong(encodable.id.getLeastSignificantBits());

                // Encoding name
                buffer.writeUTF8(encodable.name);

                // Encoding someParameter
                buffer.writeInt(encodable.someParameter);
            },
            (ctx) -> {
                FastBuffer buffer = ctx.buffer();

                // Decoding id
                UUID id = new UUID(
                        buffer.readLong(),
                        buffer.readLong()
                );

                // Decoding name
                String name = buffer.readUTF8();

                // Decoding someParameter
                int someParameter = buffer.readInt();

                return new ExampleEncodable(id, name, someParameter);
            }
    );

    @Test
    public void test() {
        ExampleEncodable exampleEncodable = new ExampleEncodable(
                UUID.randomUUID(),
                "A Name",
                42
        );

        FastBuffer buffer = FastBytes.expanding();

        // Encode to buffer
        EXAMPLE_CODEC.encode(exampleEncodable, buffer);

        // Decode from buffer
        ExampleEncodable decoded = EXAMPLE_CODEC.decode(DecodeContext.of(buffer));

        Assertions.assertEquals(exampleEncodable.id, decoded.id);
        Assertions.assertEquals(exampleEncodable.name, decoded.name);
        Assertions.assertEquals(exampleEncodable.someParameter, decoded.someParameter);
    }

}