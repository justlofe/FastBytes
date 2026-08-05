package lofe.fastbytes.buffer;

public interface FastBuffer extends FastReader, FastWriter, AutoCloseable {

    @Override
    FastBuffer writeBytes(byte[] bytes);

    @Override
    FastBuffer writeByte(byte val);

    @Override
    FastBuffer writeShort(short val);

    @Override
    FastBuffer writeInt(int val);

    @Override
    FastBuffer writeVarInt(int val);

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

    byte[] packReadable();

    @Override
    void close();
}
