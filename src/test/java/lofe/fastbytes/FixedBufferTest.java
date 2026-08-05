package lofe.fastbytes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import lofe.fastbytes.buffer.FastBuffer;
import lofe.fastbytes.codec.Codecs;
import lofe.fastbytes.codec.context.DecodeContext;

import java.util.UUID;

public class FixedBufferTest {

    @Test
    public void test() {
        try (FastBuffer buffer = FastBytes.fixed(1024)) {
            UUID uuid = UUID.randomUUID();
            Codecs.UUID.encode(uuid, buffer);
            UUID decoded = Codecs.UUID.decode(DecodeContext.of(buffer));
            Assertions.assertEquals(uuid, decoded);
            Assertions.assertThrows(UnsupportedOperationException.class, buffer::readByte);
        }
    }

}
