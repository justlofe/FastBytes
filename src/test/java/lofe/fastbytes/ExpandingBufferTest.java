package lofe.fastbytes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import lofe.fastbytes.buffer.FastBuffer;
import lofe.fastbytes.codec.Codecs;
import lofe.fastbytes.codec.context.DecodeContext;

import java.util.UUID;

public class ExpandingBufferTest {

    @Test
    public void test() {
        try (FastBuffer buffer = FastBytes.expanding()) {
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

}
