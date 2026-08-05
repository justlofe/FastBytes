package lofe.fastbytes.buffer;

public class ExpandingBuffer extends FixedBuffer {

    public ExpandingBuffer(int size) {
        super(size);
    }

    @Override
    protected void writeRawBytes(byte... bytes) {
        if(closed)
            throw new IllegalStateException("closed");

        if(wrapped.length < (writeCursor + bytes.length)) {
            int newSize = writeCursor + bytes.length + 8;
            byte[] newData = new byte[newSize];
            System.arraycopy(wrapped, 0, newData, 0, wrapped.length);
            this.wrapped = newData;
        }

        for (byte val : bytes) {
            wrapped[writeCursor++] = val;
        }
    }

}
