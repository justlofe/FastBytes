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

}
