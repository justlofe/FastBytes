package lofe.fastbytes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import lofe.fastbytes.buffer.FastBuffer;

import java.io.File;
import java.nio.file.Path;

public class FileTest {

    @Test
    public void test(@TempDir Path tempDir) {
        FastBuffer buffer = FastBytes.expanding();

        buffer.writeInt(42);
        buffer.writeFloat(24.421f);

        File file = tempDir.resolve("test.bin").toFile();

        FastBytes.writeFile(file, buffer);

        try (FastBuffer buffer2 = FastBytes.readFile(file)) {
            Assertions.assertEquals(42, buffer2.readInt());
            Assertions.assertEquals(24.421f, buffer2.readFloat());
        }

    }

}
