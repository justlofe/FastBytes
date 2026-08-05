package lofe.fastbytes.util;

import lofe.fastbytes.buffer.FastReader;
import lofe.fastbytes.buffer.FastWriter;

public final class VarIntUtil {

    private VarIntUtil() {}

    public static int read(FastReader reader) {
        int out = 0;
        int bytes = 0;

        byte in;
        do {
            in = reader.readByte();
            out |= (in & 127) << bytes++ * 7;
            if (bytes > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while(hasContinuationBit(in));

        return out;
    }

    public static void write(FastWriter output, int value) {
        if ((value & -128) == 0) output.writeByte((byte) value);
        else if ((value & -16384) == 0) {
            int s = (value & 127 | 128) << 8 | value >>> 7;
            output.writeShort((short) s);
        }
        else writeSlow(output, value);
    }

    public static boolean hasContinuationBit(byte in) {
        return (in & 128) == 128;
    }

    public static void writeSlow(FastWriter output, int value) {
        while((value & -128) != 0) {
            output.writeByte((byte) (value & 127 | 128));
            value >>>= 7;
        }

        output.writeByte((byte) value);
    }

}
