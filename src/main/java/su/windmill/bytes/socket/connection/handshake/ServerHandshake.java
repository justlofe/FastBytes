package su.windmill.bytes.socket.connection.handshake;

import su.windmill.bytes.socket.connection.WebSocketConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public final class ServerHandshake implements HandshakeBehaviour {

    public static final ServerHandshake INSTANCE = new ServerHandshake();

    private ServerHandshake() {}

    @Override
    public boolean handshake(WebSocketConnection connection) throws IOException {
        Socket socket = connection.socket();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.inputStream(), StandardCharsets.UTF_8));
        String body = br.readLine();
        if (body == null || !body.startsWith("GET ")) {
            throw new IOException("Reading handshake failed: " + body);
        }

        String line;
        String secKey = null;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            int idx = line.indexOf(":");
            if (idx > 0) {
                String name = line.substring(0, idx).trim().toLowerCase();
                String value = line.substring(idx + 1).trim();
                if ("sec-websocket-key".equals(name)) secKey = value;
            }
        }
        if (secKey == null) {
            socket.close();
            return false;
        }

        String accept;
        try {
            accept = HandshakeBehaviour.compileAccept(secKey);
        }
        catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
        String response = "HTTP/1.1 101 Switching Protocols\r\n" +
                "Upgrade: websocket\r\n" +
                "Connection: Upgrade\r\n" +
                "Sec-WebSocket-Accept: " + accept + "\r\n" +
                "\r\n";

        OutputStream outputStream = connection.outputStream();
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        return true;
    }

}
