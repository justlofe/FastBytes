package su.windmill.bytes.socket;

import java.io.*;
import java.security.SecureRandom;

public record Frame(int opcode, byte[] payload) {

    public static final int
            OPCODE_TEXT = 0x1,
            OPCODE_BINARY = 0x2,
            OPCODE_CLOSE = 0x8,
            OPCODE_PING = 0x9,
            OPCODE_PONG = 0xA;

    public static final int
            FINAL = 0x80,
            OPCODE_MASK = 0x0F,
            MASKED = FINAL,
            PAYLOAD_LENGTH = 0x7F,
            LENGTH_MASK = 0xFF;

    public static Frame read(InputStream inputStream) throws IOException {
        int first = inputStream.read();
        if(first == -1) return null;
        int second = inputStream.read();
        if(second == -1) return null;

        boolean _final = (first & FINAL) != 0;
        int opcode = first & OPCODE_MASK;
        boolean masked = (second & MASKED) != 0;

        int payloadLength = second & PAYLOAD_LENGTH;
        if(payloadLength == 126) payloadLength = (inputStream.read() << 8) | inputStream.read();
        else if (payloadLength == 127) {
            long length = 0;
            for (int i = 0; i < 8; i++) {
                length = (length << 8) | (inputStream.read() & LENGTH_MASK);
            }
            if(length > Integer.MAX_VALUE) throw new IOException("payload too big");
            payloadLength = (int) length;
        }

        byte[] maskKey = null;
        if(masked) {
            maskKey = new byte[4];
            int readBytes = inputStream.read(maskKey);
            if(readBytes != 4) throw new IOException("can't read mask key");
        }

        byte[] payload = new byte[payloadLength];
        int readBytes = 0;
        while (readBytes < payloadLength) {
            int readBytes0 = inputStream.read(payload, readBytes, payloadLength - readBytes);
            if(readBytes0 == -1) throw new EOFException();
            readBytes += readBytes0;
        }

        if(masked) {
            for (int i = 0; i < payloadLength; i++) {
                payload[i] = (byte) (payload[i] ^ maskKey[i % 4]);
            }
        }

        return new Frame(opcode, payload);
    }

    public static void write(Frame frame, OutputStream outputStream, boolean masked) throws IOException {
        byte[] payload = frame.payload();
        if(payload == null) payload = new byte[0];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int finAndOp = FINAL | (frame.opcode() & OPCODE_MASK);
        baos.write(finAndOp);

        int length = payload.length;
        if (length <= 125) baos.write(MASKED | length);
        else if (length <= 65535) {
            baos.write(MASKED | 126);
            baos.write((length >> 8) & 0xFF);
            baos.write(length & 0xFF);
        }
        else {
            baos.write(MASKED | 127);
            for (int i = 7; i >= 0; i--) baos.write((length >> (8*i)) & 0xFF);
        }

        if(!masked) baos.write(payload);
        else {
            SecureRandom random = new SecureRandom();
            byte[] maskKey = new byte[4];
            random.nextBytes(maskKey);
            baos.write(maskKey);
            for (int i = 0; i < length; i++) {
                baos.write(payload[i] ^ maskKey[i % 4]);
            }
        }

        outputStream.write(baos.toByteArray());
        outputStream.flush();
    }

}
