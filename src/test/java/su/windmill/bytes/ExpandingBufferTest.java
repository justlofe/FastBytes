package su.windmill.bytes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.codec.Codecs;
import su.windmill.bytes.codec.context.DecodeContext;

import java.util.UUID;

public class ExpandingBufferTest {

    @Test
    public void test() {
        FastBuffer buffer = FastBuffer.allocateUnpooled();
        buffer.writeInt(42);
        buffer.writeInt(982);
        buffer.writeFloat(42.67f);
        UUID someUuid = UUID.randomUUID();
        Codecs.UUID.encode(someUuid, buffer);

        Assertions.assertEquals(42, buffer.readInt());
        Assertions.assertEquals(982, buffer.readInt());
        Assertions.assertEquals(42.67f, buffer.readFloat());
        Assertions.assertEquals(someUuid, Codecs.UUID.decode(DecodeContext.of(buffer)));
    }

}
