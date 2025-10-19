package su.windmill.bytes.socket.connection;

import su.windmill.bytes.FastBytes;
import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.socket.Frame;
import su.windmill.bytes.socket.MessageWriter;
import su.windmill.bytes.socket.connection.handshake.HandshakeBehaviour;
import su.windmill.bytes.util.Either;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public final class WebSocketConnection {

    private final Socket socket;
    private final boolean serverInstance;
    private final HandshakeBehaviour handshakeBehaviour;

    private final MessageConsumer messageConsumer;
    private final ErrorConsumer errorConsumer;
    private final CloseConsumer closeConsumer;

    private final InputStream inputStream;
    private final OutputStream outputStream;

    private volatile Thread workingThread;
    private volatile boolean connected;

    public WebSocketConnection(Socket socket, boolean serverInstance, HandshakeBehaviour handshakeBehaviour, MessageConsumer messageConsumer, ErrorConsumer errorConsumer, CloseConsumer closeConsumer) throws IOException {
        this.socket = socket;
        this.serverInstance = serverInstance;
        this.handshakeBehaviour = handshakeBehaviour;

        this.messageConsumer = messageConsumer;
        this.errorConsumer = errorConsumer;
        this.closeConsumer = closeConsumer;

        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
    }

    public Socket socket() {
        return socket;
    }

    public InputStream inputStream() {
        return inputStream;
    }

    public OutputStream outputStream() {
        return outputStream;
    }

    public Thread workingThread() {
        return workingThread;
    }

    public boolean connected() {
        return connected;
    }

    public void performHandshake() {
        if(connected) return;

        try {
            if(!handshakeBehaviour.handshake(this)) return;
        }
        catch (Throwable throwable) {
            errorConsumer.accept(this, throwable);
            shutdown(true);
        }

        connected = true;
    }

    public void startReadLoop() {
        workingThread = Thread.startVirtualThread(this::read);
    }

    private void read() {
        try {
            while (connected && !socket.isClosed()) {
                Frame frame = Frame.read(inputStream);
                if (frame == null) break;

                switch (frame.opcode()) {
                    case Frame.OPCODE_TEXT -> messageConsumer.accept(this, Either.second(new String(frame.payload(), StandardCharsets.UTF_8)));
                    case Frame.OPCODE_BINARY -> {
                        byte[] payload = frame.payload();
                        FastBuffer buffer = FastBytes.fixed(payload.length);
                        buffer.writeBytes(payload);
                        messageConsumer.accept(this, Either.first(buffer));
                    }
                    case Frame.OPCODE_CLOSE -> {
                        int code = 0;
                        String reason = "";

                        byte[] payload = frame.payload();
                        int payloadLength = payload.length;
                        if (payloadLength >= 2) {
                            code = ((payload[0] & 0xFF) << 8) | (payload[1] & 0xFF);
                            reason = new String(payload, 2, payloadLength - 2, StandardCharsets.UTF_8);
                        }

                        close(code, reason);
                    }
                    case Frame.OPCODE_PING -> writeFrame(Frame.OPCODE_PONG, false, frame.payload());
                    default -> {}
                }
            }
            shutdown(true);
        }
        catch (Throwable throwable) {
            errorConsumer.accept(this, throwable);
            shutdown();
        }
    }

    public void send(MessageWriter writer) {
        FastBuffer buffer = FastBytes.expanding();
        try {
            writer.accept(buffer);
        }
        catch (Throwable throwable) {
            errorConsumer.accept(this, throwable);
        }

        writeFrame(
                Frame.OPCODE_BINARY,
                true,
                buffer.packReadable()
        );
    }

    public void sendText(String text) {
        writeFrame(
                Frame.OPCODE_TEXT,
                true,
                text.getBytes(StandardCharsets.UTF_8)
        );
    }

    public void writeFrame(int opcode, boolean _final, byte[] payload) {
        try {
            Frame.write(
                    new Frame(opcode, payload),
                    outputStream,
                    !serverInstance // clients should mask frames, servers should not
            );
        }
        catch (Throwable throwable) {
            errorConsumer.accept(this, throwable);
        }
    }

    public void close(int code, String reason) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (code > 0) {
                baos.write((code >> 8) & 0xFF);
                baos.write(code & 0xFF);
            }
            if (reason != null) baos.write(reason.getBytes());

            writeFrame(Frame.OPCODE_CLOSE, true, baos.toByteArray());
            closeConsumer.accept(this, code, reason);
            shutdown();
        }
        catch (Throwable throwable) {
            errorConsumer.accept(this, throwable);
            shutdown();
        }
    }

    public void shutdown() {
        shutdown(false);
    }

    public void shutdown(boolean force) {
        if(!connected && !force) return;
        connected = force;
        workingThread = null;
        try {
            socket.close();
        }
        catch (Throwable _) {}
    }

}
