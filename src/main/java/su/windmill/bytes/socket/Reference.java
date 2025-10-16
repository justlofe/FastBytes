package su.windmill.bytes.socket;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simple multithreaded WebSocket server (RFC6455, minimal features)
 * - Accepts multiple clients using a fixed thread pool
 * - Performs the WebSocket opening handshake
 * - Handles text/binary/ping/pong/close
 * - Demonstrates echo and broadcast utilities
 *
 * NOTE: This is a minimal educational implementation, not production hardened.
 */
public class Reference {

    private final int port;
    private final ExecutorService acceptExec = Executors.newSingleThreadExecutor();
    private final ExecutorService clientPool;
    private final Set<ClientHandler> clients = Collections.synchronizedSet(new HashSet<>());
    private volatile boolean running = false;
    private ServerSocket serverSocket;

    public Reference(int port, int maxClients) {
        this.port = port;
        this.clientPool = Executors.newFixedThreadPool(maxClients);
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        running = true;
        System.out.println("WebSocketServer listening on port " + port);

        acceptExec.execute(() -> {
            while (running) {
                try {
                    Socket s = serverSocket.accept();
                    s.setTcpNoDelay(true);
                    ClientHandler handler = new ClientHandler(s);
                    clients.add(handler);
                    clientPool.execute(handler);
                } catch (IOException e) {
                    if (running) e.printStackTrace();
                }
            }
        });
    }

    public void stop() throws IOException {
        running = false;
        try { serverSocket.close(); } catch (Exception ignored) {}
        clientPool.shutdownNow();
        acceptExec.shutdownNow();
        synchronized (clients) {
            for (ClientHandler c : clients) c.shutdown();
            clients.clear();
        }
    }

    /** Broadcast a text message to all connected clients */
    public void broadcastText(String message) {
        synchronized (clients) {
            for (ClientHandler c : clients) c.sendText(message);
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private InputStream in;
        private OutputStream out;
        private volatile boolean open = false;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
                if (!handshake()) return;
                open = true;
                System.out.println("Client connected: " + socket.getRemoteSocketAddress());

                // Main read loop
                while (open && !socket.isClosed()) {
                    Frame f = readFrame(in);
                    if (f == null) break;
                    switch (f.opcode) {
                        case 0x1: // text
                            String text = new String(f.payload, StandardCharsets.UTF_8);
                            // echo back
                            sendText(text);
                            break;
                        case 0x2: // binary
                            sendBinary(f.payload); // echo back
                            break;
                        case 0x8: // close
                            int code = 0;
                            String reason = "";
                            if (f.payload.length >= 2) {
                                code = ((f.payload[0] & 0xFF) << 8) | (f.payload[1] & 0xFF);
                                if (f.payload.length > 2) reason = new String(f.payload, 2, f.payload.length - 2, StandardCharsets.UTF_8);
                            }
                            // respond with close and shutdown
                            sendFrame((byte)0x8, f.payload, true);
                            open = false;
                            break;
                        case 0x9: // ping -> pong
                            sendFrame((byte)0xA, f.payload, true);
                            break;
                        case 0xA: // pong - ignore
                            break;
                        default:
                            // ignoring continuation/other opcodes in this simple server
                            break;
                    }
                }
            } catch (SocketException se) {
                // normal on client disconnect
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                shutdown();
            }
        }

        private boolean handshake() throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String requestLine = br.readLine();
            if (requestLine == null || !requestLine.startsWith("GET ")) {
                socket.close();
                return false;
            }
            String line;
            String secKey = null;
            String host = null;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                int idx = line.indexOf(":");
                if (idx > 0) {
                    String name = line.substring(0, idx).trim().toLowerCase();
                    String value = line.substring(idx + 1).trim();
                    if ("sec-websocket-key".equals(name)) secKey = value;
                    if ("host".equals(name)) host = value;
                }
            }
            if (secKey == null) {
                socket.close();
                return false;
            }
            String accept = computeAccept(secKey);
            String response = "HTTP/1.1 101 Switching Protocols\r\n" +
                    "Upgrade: websocket\r\n" +
                    "Connection: Upgrade\r\n" +
                    "Sec-WebSocket-Accept: " + accept + "\r\n" +
                    "\r\n";
            out.write(response.getBytes(StandardCharsets.UTF_8));
            out.flush();
            return true;
        }

        void sendText(String text) {
            sendFrame((byte)0x1, text.getBytes(StandardCharsets.UTF_8), true);
        }

        void sendBinary(byte[] data) {
            sendFrame((byte)0x2, data, true);
        }

        /**
         * Send a frame from server to client. Servers MUST NOT mask the payload.
         * @param opcode opcode byte
         * @param payload payload bytes (may be null)
         * @param flush whether to flush the output after write
         */
        void sendFrame(byte opcode, byte[] payload, boolean flush) {
            if (payload == null) payload = new byte[0];
            try {
                ByteArrayOutputStream frame = new ByteArrayOutputStream();
                int finAndOp = 0x80 | (opcode & 0x0F);
                frame.write(finAndOp);

                int payloadLen = payload.length;
                if (payloadLen <= 125) {
                    frame.write(payloadLen); // no mask bit for server
                } else if (payloadLen <= 65535) {
                    frame.write(126);
                    frame.write((payloadLen >> 8) & 0xFF);
                    frame.write(payloadLen & 0xFF);
                } else {
                    frame.write(127);
                    for (int i = 7; i >= 0; i--) frame.write((payloadLen >> (8 * i)) & 0xFF);
                }
                frame.write(payload);

                synchronized (out) {
                    out.write(frame.toByteArray());
                    if (flush) out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                shutdown();
            }
        }

        private void shutdown() {
            open = false;
            clients.remove(this);
            try { socket.close(); } catch (Exception ignored) {}
            System.out.println("Client disconnected: " + socket.getRemoteSocketAddress());
        }

        // Minimal frame reader for client -> server frames (clients MUST mask)
        private Frame readFrame(InputStream in) throws IOException {
            int b1 = in.read();
            if (b1 == -1) return null;
            int b2 = in.read();
            if (b2 == -1) return null;

            boolean fin = (b1 & 0x80) != 0;
            int opcode = b1 & 0x0F;
            boolean masked = (b2 & 0x80) != 0;
            int payloadLen = b2 & 0x7F;

            if (payloadLen == 126) {
                int hi = in.read();
                int lo = in.read();
                payloadLen = (hi << 8) | lo;
            } else if (payloadLen == 127) {
                long len = 0;
                for (int i = 0; i < 8; i++) {
                    len = (len << 8) | (in.read() & 0xFF);
                }
                if (len > Integer.MAX_VALUE) throw new IOException("Payload too large");
                payloadLen = (int) len;
            }

            byte[] maskKey = null;
            if (masked) {
                maskKey = new byte[4];
                int r = in.read(maskKey);
                if (r != 4) throw new IOException("Could not read mask key");
            } else {
                // per protocol clients MUST mask, but we'll continue (some non-compliant clients exist)
                // throw new IOException("Client frames must be masked");
            }

            byte[] payload = new byte[payloadLen];
            int readSoFar = 0;
            while (readSoFar < payloadLen) {
                int r = in.read(payload, readSoFar, payloadLen - readSoFar);
                if (r == -1) throw new EOFException("Stream ended");
                readSoFar += r;
            }

            if (masked && maskKey != null) {
                for (int i = 0; i < payload.length; i++) payload[i] = (byte)(payload[i] ^ maskKey[i % 4]);
            }

            return new Frame(opcode, payload);
        }
    }

    private static class Frame {
        final int opcode;
        final byte[] payload;
        Frame(int opcode, byte[] payload) { this.opcode = opcode; this.payload = payload; }
    }

    private static String computeAccept(String key) throws IOException {
        try {
            String magic = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] digest = sha1.digest((key + magic).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    // Simple main to start the server
//    public static void main(String[] args) throws Exception {
//        int port = 8887;
//        int maxClients = 50;
//        if (args.length >= 1) port = Integer.parseInt(args[0]);
//        if (args.length >= 2) maxClients = Integer.parseInt(args[1]);
//
//        WebSocketServer server = new WebSocketServer(port, maxClients);
//        server.start();
//
//        // simple console commands: 'q' to quit, 'b <msg>' to broadcast
//        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
//        String line;
//        System.out.println("Type 'q' to stop, or 'b your message' to broadcast.");
//        while ((line = console.readLine()) != null) {
//            if (line.equalsIgnoreCase("q")) break;
//            if (line.startsWith("b ")) server.broadcastText(line.substring(2));
//        }
//
//        server.stop();
//        System.out.println("Server stopped");
//    }
}
