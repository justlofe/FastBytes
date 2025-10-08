package su.windmill.bytes;

import su.windmill.bytes.buffer.ExpandingBuffer;
import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.buffer.FixedBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FastBytes {

    private FastBytes() {}

    public static void writeFile(File file, FastBuffer buffer) {
        File folder = file.getParentFile();
        if(!folder.exists() && !folder.mkdirs()) throw new IllegalArgumentException("Invalid file path: " + file.getPath());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            int readableBytes = buffer.readableBytes();
            for (int i = 0; i < readableBytes; i++) {
                fos.write(buffer.readByte());
            }
            fos.flush();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static FastBuffer readFile(File file) {
        if(!file.exists()) throw new IllegalArgumentException("File doesn't exists");
        try (FileInputStream fis = new FileInputStream(file)) {
            return readStream(fis);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static FastBuffer readStream(InputStream is) {
        FastBuffer expanding = expanding();
        try {
            int val;
            while ((val = is.read()) != -1) {
                expanding.writeByte((byte) val);
            }
            return expanding;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static FastBuffer expanding() {
        return expanding(1024);
    }

    public static FastBuffer expanding(int size) {
        return allocate(size, true);
    }

    public static FastBuffer fixed(int size) {
        return allocate(size, false);
    }

    public static FastBuffer allocate(int size, boolean expanding) {
        return expanding
                ? new ExpandingBuffer(size)
                : new FixedBuffer(size);
    }

}
