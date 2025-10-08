import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import su.windmill.buffer.FastBuffer;
import su.windmill.codec.Codecs;
import su.windmill.codec.context.DecodeContext;

import java.util.UUID;

public class BufferTest {

    @Test
    public void testEncodeDecode() {
        FastBuffer buffer = FastBuffer.allocate(1024);
        UUID uuid = UUID.randomUUID();
        Codecs.UUID.encode(uuid, buffer);
        UUID decoded = Codecs.UUID.decode(DecodeContext.of(buffer));
        Assertions.assertEquals(uuid, decoded);
    }

}
