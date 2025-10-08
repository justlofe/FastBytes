package su.windmill.bytes.buffer;

import java.nio.charset.StandardCharsets;

public class FixedBuffer implements FastBuffer {

    protected byte[] data;
    protected final boolean onlyRead;
    protected transient int readCursor, writeCursor;

    public FixedBuffer(byte[] data, boolean onlyRead) {
        if(data == null) throw new IllegalArgumentException("data can't be null");
        this.data = data;
        this.onlyRead = onlyRead;
        this.readCursor = 0;
        this.writeCursor = 0;
    }

    @Override
    public FixedBuffer writeByte(byte val) {
        writeBytes(val);
        return this;
    }

    @Override
    public FixedBuffer writeShort(short val) {
        writeBytes(
                (byte) (val >> 8),
                (byte) val
        );
        return this;
    }

    @Override
    public FixedBuffer writeInt(int val) {
        writeBytes(
                (byte) (val >> 24),
                (byte) (val >> 16),
                (byte) (val >> 8),
                (byte) val
        );
        return this;
    }

    @Override
    public FixedBuffer writeLong(long val) {
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
    public FixedBuffer writeFloat(float val) {
        return writeInt(Float.floatToIntBits(val));
    }

    @Override
    public FixedBuffer writeDouble(double val) {
        return writeLong(Double.doubleToLongBits(val));
    }

    @Override
    public FixedBuffer writeBoolean(boolean val) {
        return writeByte((byte) (val ? 1 : 0));
    }

    @Override
    public FixedBuffer writeChar(char val) {
        return writeShort((short) val);
    }

    @Override
    public FastBuffer writeUTF8(String val) {
        byte[] bytes = val.getBytes(StandardCharsets.UTF_8);
        writeInt(bytes.length);
        writeBytes(bytes);
        return this;
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

    @Override
    public char readChar() {
        return (char) readShort();
    }

    @Override
    public String readUTF8() {
        byte[] bytes = new byte[readInt()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = peekByte();
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    protected void writeBytes(byte... bytes) {
        if(onlyRead) throw new UnsupportedOperationException("only read");
        if(!hasBytes(writeCursor, bytes.length)) throwEndOfData();
        for (byte val : bytes) {
            data[writeCursor++] = val;
        }
    }

    private byte peekByte() {
        if(!hasBytes(readCursor, 1)) throwEndOfData();
        return data[readCursor++];
    }

    private void throwEndOfData() {
        throw new UnsupportedOperationException("end of data");
    }

    protected boolean hasBytes(int cursor, int count) {
        return data.length >= (cursor + count);
    }

}
