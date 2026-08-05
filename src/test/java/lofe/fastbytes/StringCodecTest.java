package lofe.fastbytes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import lofe.fastbytes.buffer.FastBuffer;

import java.util.UUID;

public class StringCodecTest {

    @Test
    public void test() {
        try (FastBuffer buffer = FastBytes.expanding()) {
            String testString = UUID.randomUUID().toString();
            buffer.writeUTF8(testString);
            Assertions.assertEquals(testString, buffer.readUTF8());
        }
    }

}
