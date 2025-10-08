package su.windmill.bytes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import su.windmill.bytes.buffer.FastBuffer;

import java.util.UUID;

public class StringCodecTest {

    @Test
    public void test() {
        FastBuffer buffer = FastBytes.expanding();
        String testString = UUID.randomUUID().toString();
        buffer.writeUTF8(testString);
        Assertions.assertEquals(testString, buffer.readUTF8());
    }

}
