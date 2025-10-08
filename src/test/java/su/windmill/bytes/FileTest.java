package su.windmill.bytes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import su.windmill.bytes.buffer.FastBuffer;

import java.io.File;
import java.net.URISyntaxException;

public class FileTest {

    @Test
    public void test() throws URISyntaxException {
        FastBuffer buffer = FastBytes.expanding();

        buffer.writeInt(42);
        buffer.writeFloat(24.421f);

        File file = new File(
                new File(FileTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile(),
                "test.bin"
        );

        FastBytes.writeFile(file, buffer);

        FastBuffer buffer2 = FastBytes.readFile(file);
        Assertions.assertEquals(42, buffer2.readInt());
        Assertions.assertEquals(24.421f, buffer2.readFloat());
    }

}
