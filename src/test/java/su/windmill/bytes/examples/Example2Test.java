package su.windmill.bytes.examples;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import su.windmill.bytes.FastBytes;
import su.windmill.bytes.buffer.FastBuffer;

import java.io.File;

public class Example2Test {

    @Test
    public void test() {
        FastBuffer buffer = FastBytes.expanding();

        buffer.writeInt(42);
        buffer.writeFloat(24.421f);

        File file;
        try {
            file = new File(
                    new File(Example2Test.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile(),
                    "test.bin"
            );
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        FastBytes.writeFile(file, buffer);

        FastBuffer buffer2 = FastBytes.readFile(file);
        Assertions.assertEquals(42, buffer2.readInt());
        Assertions.assertEquals(24.421f, buffer2.readFloat());
    }

}
