package su.windmill.bytes.socket.connection.handshake;

import su.windmill.bytes.socket.connection.WebSocketConnection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public sealed interface HandshakeBehaviour permits ServerHandshake, ClientHandshake {

    boolean handshake(WebSocketConnection connection) throws IOException;

    static String compileAccept(String key) throws Exception {
        String magic = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] digest = sha1.digest((key + magic).getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(digest);
    }

}
