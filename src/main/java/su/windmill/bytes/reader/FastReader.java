package su.windmill.bytes.reader;

public interface FastReader {

    byte readByte();
    short readShort();
    int readInt();
    long readLong();
    float readFloat();
    double readDouble();
    boolean readBoolean();
    char readChar();
    String readUTF8();

    /**
     * Return count of readable bytes
     */
    int readableBytes();

}
