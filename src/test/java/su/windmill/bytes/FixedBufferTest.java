package su.windmill.bytes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.codec.Codecs;
import su.windmill.bytes.codec.context.DecodeContext;

import java.util.UUID;

public class FixedBufferTest {

    @Test
    public void test() {
        FastBuffer buffer = FastBytes.fixed(1024);
        UUID uuid = UUID.randomUUID();
        Codecs.UUID.encode(uuid, buffer);
        UUID decoded = Codecs.UUID.decode(DecodeContext.of(buffer));
        Assertions.assertEquals(uuid, decoded);
        Assertions.assertThrows(UnsupportedOperationException.class, buffer::readByte);
    }

}
