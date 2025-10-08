package su.windmill.bytes.writer;

public interface FastWriter {

    FastWriter writeByte(byte val);
    FastWriter writeShort(short val);
    FastWriter writeInt(int val);
    FastWriter writeLong(long val);
    FastWriter writeFloat(float val);
    FastWriter writeDouble(double val);
    FastWriter writeBoolean(boolean val);
    FastWriter writeChar(char val);
    FastWriter writeUTF8(String val);

}
