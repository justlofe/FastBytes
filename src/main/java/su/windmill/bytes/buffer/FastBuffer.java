package su.windmill.bytes.buffer;

import su.windmill.bytes.reader.FastReader;
import su.windmill.bytes.writer.FastWriter;

public interface FastBuffer extends FastReader, FastWriter {

    @Override
    FastBuffer writeByte(byte val);

    @Override
    FastBuffer writeShort(short val);

    @Override
    FastBuffer writeInt(int val);

    @Override
    FastBuffer writeLong(long val);

    @Override
    FastBuffer writeFloat(float val);

    @Override
    FastBuffer writeDouble(double val);

    @Override
    FastBuffer writeBoolean(boolean val);

    @Override
    FastBuffer writeChar(char val);

    @Override
    FastBuffer writeUTF8(String val);

    static FastBuffer allocateUnpooled() {
        return allocateExpanding(1024);
    }

    static ExpandingBuffer allocateExpanding(int size) {
        return allocateExpanding(size, false);
    }

    static ExpandingBuffer allocateExpanding(int size, boolean onlyRead) {
        return new ExpandingBuffer(new byte[size], onlyRead);
    }

    static FixedBuffer allocateFixed(int size) {
        return allocateFixed(size, false);
    }

    static FixedBuffer allocateFixed(int size, boolean onlyRead) {
        return new FixedBuffer(new byte[size], onlyRead);
    }

}
