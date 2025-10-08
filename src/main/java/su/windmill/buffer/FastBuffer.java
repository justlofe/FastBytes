package su.windmill.buffer;

import su.windmill.reader.FastReader;
import su.windmill.writer.FastWriter;

import java.io.IOException;
import java.io.InputStream;

public class FastBuffer implements FastReader, FastWriter {

    protected final transient byte[] data;
    protected final boolean onlyRead;
    protected transient int readCursor, writeCursor;

    public FastBuffer(byte[] data, boolean onlyRead) {
        if(data == null) throw new IllegalArgumentException("data can't be null");
        this.data = data;
        this.onlyRead = onlyRead;
        this.readCursor = 0;
        this.writeCursor = 0;
    }

    @Override
    public FastBuffer writeByte(byte val) {
        writeBytes(val);
        return this;
    }

    @Override
    public FastBuffer writeShort(short val) {
        writeBytes(
                (byte) (val >> 8),
                (byte) val
        );
        return this;
    }

    @Override
    public FastBuffer writeInt(int val) {
        writeBytes(
                (byte) (val >> 24),
                (byte) (val >> 16),
                (byte) (val >> 8),
                (byte) val
        );
        return this;
    }

    @Override
    public FastBuffer writeLong(long val) {
        writeBytes(
                (byte) (val >> 56),
                (byte) (val >> 48),
                (byte) (val >> 40),
                (byte) (val >> 32),
                (byte) (val >> 24),
                (byte) (val >> 16),
                (byte) (val >> 8),
                (byte) val
        );
        return this;
    }

    @Override
    public FastBuffer writeFloat(float val) {
        return writeInt(Float.floatToIntBits(val));
    }

    @Override
    public FastBuffer writeDouble(double val) {
        return writeLong(Double.doubleToLongBits(val));
    }

    @Override
    public FastBuffer writeBoolean(boolean val) {
        return writeByte((byte) (val ? 1 : 0));
    }

    @Override
    public byte readByte() {
        return peekByte();
    }

    @Override
    public short readShort() {
        if(!hasBytes(readCursor, 2)) throwEndOfData();
        return (short) (((peekByte() & 0xFF) << 8) |
                (peekByte() & 0xFF));
    }

    @Override
    public int readInt() {
        if(!hasBytes(readCursor, 4)) throwEndOfData();
        return ((peekByte() & 0xFF) << 24) |
                ((peekByte() & 0xFF) << 16) |
                ((peekByte() & 0xFF) << 8) |
                (peekByte() & 0xFF);
    }

    @Override
    public long readLong() {
        if (!hasBytes(readCursor, 8)) throwEndOfData();
        return ((peekByte() & 0xFFL) << 56) |
                ((peekByte() & 0xFFL) << 48) |
                ((peekByte() & 0xFFL) << 40) |
                ((peekByte() & 0xFFL) << 32) |
                ((peekByte() & 0xFFL) << 24) |
                ((peekByte() & 0xFFL) << 16) |
                ((peekByte() & 0xFFL) << 8) |
                (peekByte() & 0xFFL);
    }

    @Override
    public float readFloat() {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public double readDouble() {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public boolean readBoolean() {
        return readByte() == 1;
    }

    private void writeBytes(byte... bytes) {
        if(onlyRead) throw new UnsupportedOperationException("only read");
        if(!hasBytes(writeCursor, bytes.length)) throwEndOfData();
        for (byte val : bytes) {
            data[writeCursor++] = val;
        }
    }

    private void writeBytes(byte[] source, int offset, int length) {
        for (int i = 0; i < length; i++) {
            data[writeCursor++] = source[offset + i];
        }
    }

    private byte peekByte() {
        if(!hasBytes(readCursor, 1)) throwEndOfData();
        return data[readCursor++];
    }

    private void throwEndOfData() {
        throw new UnsupportedOperationException("end of data");
    }

    private boolean hasBytes(int cursor, int count) {
        return data.length >= (cursor + count);
    }

    public static FastBuffer readStream(InputStream is, int maxSize) {
        byte[] bytes = new byte[Math.min(maxSize, 8192)];
        int cursor = 0;
        int val;
        try {
            while ((val = is.read()) != -1) {
                bytes[cursor++] = (byte) val;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FastBuffer buffer = FastBuffer.allocate(cursor + 1);
        buffer.writeBytes(bytes, 0, buffer.data.length);
        return buffer;
    }

    public static FastBuffer allocate(int size) {
        return allocate(size, false);
    }

    public static FastBuffer allocate(int size, boolean onlyRead) {
        return new FastBuffer(new byte[size], onlyRead);
    }

}
