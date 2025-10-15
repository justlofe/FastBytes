package su.windmill.bytes.writer;

public interface FastWriter {

    /**
     * Writes bytes from an array
     */
    FastWriter writeBytes(byte[] bytes);

    /**
     * Writes one byte
     */
    FastWriter writeByte(byte val);

    /**
     * Writes short (2 bytes)
     */
    FastWriter writeShort(short val);

    /**
     * Writes int (4 bytes)
     */
    FastWriter writeInt(int val);

    /**
     * Writes long (8 bytes)
     */
    FastWriter writeLong(long val);

    /**
     * Writes float (4 bytes)
     */
    FastWriter writeFloat(float val);

    /**
     * Writes double (8 bytes)
     */
    FastWriter writeDouble(double val);

    /**
     * Writes boolean (1 byte)
     */
    FastWriter writeBoolean(boolean val);

    /**
     * Writes char (2 bytes)
     */
    FastWriter writeChar(char val);

    /**
     * Writes UTF-8 String (depends on string length, each symbol equals 1 byte)
     */
    FastWriter writeUTF8(String val);

}
