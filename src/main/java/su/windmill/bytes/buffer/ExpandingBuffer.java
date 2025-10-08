package su.windmill.bytes.buffer;

public class ExpandingBuffer extends FixedBuffer {
    public ExpandingBuffer(byte[] data, boolean onlyRead) {
        super(data, onlyRead);
    }

    @Override
    protected void writeBytes(byte... bytes) {
        if(onlyRead) throw new UnsupportedOperationException("only read");

        if(!hasBytes(writeCursor, bytes.length)) {
            int newSize = writeCursor + bytes.length + 8;
            byte[] newData = new byte[newSize];
            System.arraycopy(data, 0, newData, 0, data.length);
            this.data = newData;
        }

        for (byte val : bytes) {
            data[writeCursor++] = val;
        }
    }

}
