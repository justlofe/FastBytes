package su.windmill.bytes.buffer;

public class ExpandingBuffer extends FixedBuffer {

    public ExpandingBuffer(int size) {
        super(size);
    }

    @Override
    protected void writeRawBytes(byte... bytes) {
        if(data.length < (writeCursor + bytes.length)) {
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
