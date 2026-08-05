package lofe.fastbytes;

import lofe.fastbytes.buffer.ExpandingBuffer;
import lofe.fastbytes.buffer.FastBuffer;
import lofe.fastbytes.buffer.FixedBuffer;
import lofe.fastbytes.buffer.RedirectBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public final class FastBytes {

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

    public static ExpandingBuffer readFile(File file) {
        if(!file.exists()) throw new IllegalArgumentException("File doesn't exists");

        try (FileInputStream fis = new FileInputStream(file)) {
            return readStream(fis);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ExpandingBuffer readStream(InputStream is) {
        ExpandingBuffer expanding = expanding();
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

    public static RedirectBuffer redirect(FastBuffer buffer) {
        return new RedirectBuffer(buffer);
    }

    public static FixedBuffer wrapped(byte[] wrapped) {
        return new FixedBuffer(wrapped, 0, wrapped.length);
    }

    public static ExpandingBuffer expanding() {
        return expanding(1024);
    }

    public static ExpandingBuffer expanding(int size) {
        return (ExpandingBuffer) allocate(size, true);
    }

    public static FixedBuffer fixed(int size) {
        return allocate(size, false);
    }

    public static FixedBuffer allocate(int size, boolean expanding) {
        return expanding
                ? new ExpandingBuffer(size)
                : new FixedBuffer(size);
    }

}
