package su.windmill.bytes.socket.client;

import su.windmill.bytes.FastBytes;
import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.socket.Frame;
import su.windmill.bytes.socket.exception.ListenerCallException;
import su.windmill.bytes.socket.listener.context.*;
import su.windmill.bytes.util.Assertions;
import su.windmill.bytes.util.Key;
import su.windmill.bytes.socket.ListenerService;
import su.windmill.bytes.socket.MessageWriter;
import su.windmill.bytes.socket.listener.Listener;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractWebSocketClient implements WebSocketClient {

    private final URI uri;
    private final Frame.Submitter frameSubmitter;

    private final ExecutorService executorService;
    private final ListenerService listenerService;

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private volatile boolean connected;

    public AbstractWebSocketClient(URI uri) {
        this(
                uri,
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()),
                new ListenerService()
        );
    }

    public AbstractWebSocketClient(URI uri, ExecutorService executorService, ListenerService listenerService) {
        Assertions.notNull(uri, "uri");
        Assertions.notNull(executorService, "executorService");
        Assertions.notNull(listenerService, "listenerService");
        this.uri = uri;
        this.frameSubmitter = payload -> {
            if(outputStream == null) return;
            synchronized (outputStream) {
                outputStream.write(payload);
                outputStream.flush();
            }
        };
        this.executorService = executorService;
        this.listenerService = listenerService;
    }

    @Override
    public void connect() throws IOException {
        String host = uri.getHost();
        int port = uri.getPort() == -1 ? (uri.getScheme().equals("wss") ? 443 : 80) : uri.getPort();
        socket = new Socket(host, port);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        // Send handshake
        String key = createKey();
        String request = createConnectionRequest(host, port, key);
        outputStream.write(request.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();

        String accept = readConnectionResponse();
        String expected;
        try {
            expected = recreateAccept(key);
        }
        catch (Throwable throwable) {
            error(throwable);
            socket.close();
            return;
        }

        if (!expected.equals(accept)) throw new IOException("Invalid Sec-WebSocket-Accept. expected=\"" + expected + "\" got=\"" + accept + "\"");

        connected = true;
        listenerService.call(ContextType.OPEN, new WebSocketContext(this));

        // Start read loop
        executorService.execute(this::readLoop);
    }

    private String createConnectionRequest(String host, int port, String key) {
        String path = uri.getPath();
        if (path == null || path.isEmpty()) path = "/";

        String query = uri.getQuery();
        if (query != null) path += "?" + query;

        return "GET " + path + " HTTP/1.1\r\n" +
                "Host: " + host + (port != 80 ? ":" + port : "") + "\r\n" +
                "Upgrade: websocket\r\n" +
                "Connection: Upgrade\r\n" +
                "Sec-WebSocket-Key: " + key + "\r\n" +
                "Sec-WebSocket-Version: 13\r\n" +
                "\r\n";
    }

    private static String recreateAccept(String key) throws Exception {
        String magic = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] digest = sha1.digest((key + magic).getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(digest);
    }

    private String readConnectionResponse() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String status = br.readLine();
        if (status == null || !status.contains("101")) throw new IOException("Handshake failed: " + status);
        String line;
        String accept = null;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            int idx = line.indexOf(":");
            if (idx > 0) {
                String name = line.substring(0, idx).trim().toLowerCase();
                String value = line.substring(idx + 1).trim();
                if ("sec-websocket-accept".equals(name)) accept = value;
            }
        }
        return accept;
    }

    private void readLoop() {
        try {
            while (connected && !socket.isClosed()) {
                Frame frame = Frame.read(inputStream);
                if (frame == null) break;
                switch (frame.opcode()) {
                    case Frame.OPCODE_TEXT -> {
                        FastBuffer buffer = FastBytes.expanding();
                        buffer.writeUTF8(new String(frame.payload(), StandardCharsets.UTF_8));
                        message(buffer, true);
                    }
                    case Frame.OPCODE_BINARY -> {
                        byte[] payload = frame.payload();
                        FastBuffer buffer = FastBytes.fixed(payload.length);
                        buffer.writeBytes(payload);
                        message(buffer, false);
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
        }
        catch (Throwable throwable) {
            error(throwable);
            try {
                socket.close();
            }
            catch (Throwable _) {}
        }
    }

    private void writeFrame(int opcode, boolean _final, byte[] payload) {
        try {
            Frame.write(
                    new Frame(opcode, payload),
                    frameSubmitter
            );
        }
        catch (Throwable throwable) {
            error(throwable);
        }
    }

    private void close(int code, String reason) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (code > 0) {
                baos.write((code >> 8) & 0xFF);
                baos.write(code & 0xFF);
            }
            if (reason != null) baos.write(reason.getBytes());

            writeFrame(Frame.OPCODE_CLOSE, true, baos.toByteArray());
            connected = false;
            listenerService.call(ContextType.CLOSE, new CloseContext(this, code, reason));
            socket.close();
        }
        catch (Throwable throwable) {
            error(throwable);
            try {
                socket.close();
            }
            catch (Throwable _) {}
        }
    }

    protected void error(Throwable throwable) {
        if(throwable instanceof ListenerCallException) return;
        listenerService.call(ContextType.ERROR, new ErrorContext(this, throwable));
    }

    private void message(FastBuffer buffer, boolean textMessage) {
        listenerService.call(ContextType.MESSAGE, new MessageContext(this, buffer, textMessage));
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void send(MessageWriter writer) {
        FastBuffer buffer = FastBytes.expanding();
        try {
            writer.accept(buffer);
        }
        catch (Throwable throwable) {
            error(throwable);
        }

        writeFrame(
                Frame.OPCODE_BINARY,
                true,
                buffer.packReadable()
        );
    }

    @Override
    public void sendText(String text) {
        writeFrame(
                Frame.OPCODE_TEXT,
                true,
                text.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public InetSocketAddress remoteSocketAddress() {
        return (InetSocketAddress) socket.getRemoteSocketAddress();
    }

    @Override
    public InetSocketAddress localSocketAddress() {
        return (InetSocketAddress) socket.getLocalSocketAddress();
    }

    @Override
    public void ping() {
        writeFrame(
                Frame.OPCODE_PING,
                true,
                new byte[0]
        );
    }

    @Override
    public void close() {
        this.close(CLOSE_NORMAL, Optional.empty());
    }

    @Override
    public void close(int code, Optional<String> reason) {
        close(code, reason.orElse(null));
    }

    @Override
    public <C extends ListenerContext, L extends Listener<C>> void addListener(Key key, ContextType<C> type, L listener) {
        listenerService.addListener(key, type, listener);
    }

    @Override
    public void removeListener(Key key, ContextType<?> type) {
        listenerService.removeListener(key, type);
    }

    private static String createKey() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

}
