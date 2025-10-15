package su.windmill.bytes.buffer;

import su.windmill.bytes.writer.FastWriter;

public class RedirectBuffer implements FastBuffer {

    protected final FastBuffer buffer;

    public RedirectBuffer(FastBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public boolean readBoolean() {
        return buffer.readBoolean();
    }

    @Override
    public char readChar() {
        return buffer.readChar();
    }

    @Override
    public String readUTF8() {
        return buffer.readUTF8();
    }

    @Override
    public int readableBytes() {
        return buffer.readableBytes();
    }

    @Override
    public double readDouble() {
        return buffer.readDouble();
    }

    @Override
    public float readFloat() {
        return buffer.readFloat();
    }

    @Override
    public long readLong() {
        return buffer.readLong();
    }

    @Override
    public int readInt() {
        return buffer.readInt();
    }

    @Override
    public short readShort() {
        return buffer.readShort();
    }

    @Override
    public void readBytes(byte[] bytes) {
        buffer.readBytes(bytes);
    }

    @Override
    public byte readByte() {
        return buffer.readByte();
    }

    @Override
    public FastBuffer writeBoolean(boolean val) {
        return buffer.writeBoolean(val);
    }

    @Override
    public FastBuffer writeChar(char val) {
        return buffer.writeChar(val);
    }

    @Override
    public FastBuffer writeUTF8(String val) {
        return buffer.writeUTF8(val);
    }

    @Override
    public byte[] packReadable() {
        return buffer.packReadable();
    }

    @Override
    public FastBuffer writeDouble(double val) {
        return buffer.writeDouble(val);
    }

    @Override
    public FastBuffer writeFloat(float val) {
        return buffer.writeFloat(val);
    }

    @Override
    public FastBuffer writeLong(long val) {
        return buffer.writeLong(val);
    }

    @Override
    public FastBuffer writeInt(int val) {
        return buffer.writeInt(val);
    }

    @Override
    public FastBuffer writeShort(short val) {
        return buffer.writeShort(val);
    }

    @Override
    public FastBuffer writeBytes(byte[] bytes) {
        return buffer.writeBytes(bytes);
    }

    @Override
    public FastBuffer writeByte(byte val) {
        return buffer.writeByte(val);
    }



}
