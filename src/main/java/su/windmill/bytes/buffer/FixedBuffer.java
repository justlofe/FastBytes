package su.windmill.bytes.buffer;

import java.nio.charset.StandardCharsets;

public class FixedBuffer implements FastBuffer {

    protected byte[] data;
    protected transient int readCursor, writeCursor;

    public FixedBuffer(int size) {
        this.data = new byte[size];
        this.readCursor = 0;
        this.writeCursor = 0;
    }

    @Override
    public FixedBuffer writeBytes(byte[] bytes) {
        writeRawBytes(bytes);
        return this;
    }

    @Override
    public FixedBuffer writeByte(byte val) {
        writeRawBytes(val);
        return this;
    }

    @Override
    public FixedBuffer writeShort(short val) {
        writeRawBytes(
                (byte) (val >> 8),
                (byte) val
        );
        return this;
    }

    @Override
    public FixedBuffer writeInt(int val) {
        writeRawBytes(
                (byte) (val >> 24),
                (byte) (val >> 16),
                (byte) (val >> 8),
                (byte) val
        );
        return this;
    }

    @Override
    public FixedBuffer writeLong(long val) {
        writeRawBytes(
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
        writeRawBytes(bytes);
        return this;
    }

    @Override
    public byte[] packReadable() {
        byte[] bytes = new byte[readableBytes()];
        readBytes(bytes);
        return bytes;
    }

    @Override
    public void readBytes(byte[] bytes) {
        int length = bytes.length;
        if(!hasBytes(length)) throwEndOfData();
        for (int i = 0; i < length; i++) {
            bytes[i] = readByte();
        }
    }

    @Override
    public byte readByte() {
        return peekByte();
    }

    @Override
    public short readShort() {
        if(!hasBytes(2)) throwEndOfData();
        return (short) (((peekByte() & 0xFF) << 8) |
                (peekByte() & 0xFF));
    }

    @Override
    public int readInt() {
        if(!hasBytes(4)) throwEndOfData();
        return ((peekByte() & 0xFF) << 24) |
                ((peekByte() & 0xFF) << 16) |
                ((peekByte() & 0xFF) << 8) |
                (peekByte() & 0xFF);
    }

    @Override
    public long readLong() {
        if (!hasBytes(8)) throwEndOfData();
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

    @Override
    public int readableBytes() {
        return writeCursor - readCursor;
    }

    protected void writeRawBytes(byte... bytes) {
        if(data.length < (writeCursor + bytes.length)) throwEndOfData();
        for (byte val : bytes) {
            data[writeCursor++] = val;
        }
    }

    private byte peekByte() {
        if(!hasBytes(1)) throwEndOfData();
        return data[readCursor++];
    }

    private void throwEndOfData() {
        throw new UnsupportedOperationException("end of data");
    }

    private boolean hasBytes(int count) {
        return writeCursor >= readCursor + count;
    }

}
