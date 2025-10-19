package su.windmill.bytes.socket.connection.handshake;

import su.windmill.bytes.socket.connection.WebSocketConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public record ClientHandshake(URI uri, String host, int port) implements HandshakeBehaviour {

    @Override
    public boolean handshake(WebSocketConnection connection) throws IOException {
        // Send handshake
        String key = createKey();
        String request = createConnectionRequest(host, port, key);

        OutputStream outputStream = connection.outputStream();
        outputStream.write(request.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();

        // get response
        String accept = readConnectionResponse(connection);
        String expected;
        try {
            expected = HandshakeBehaviour.compileAccept(key);
        }
        catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

        return expected.equals(accept);
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

    private String readConnectionResponse(WebSocketConnection connection) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.inputStream(), StandardCharsets.UTF_8));
        String body = br.readLine();
        if (body == null || !body.contains("101")) throw new IOException("Reading handshake response failed: " + body);

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

    private static String createKey() {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[16];
        random.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

}
