package su.windmill.bytes.socket.expansion;

import java.util.UUID;

public class ResponseTimeoutException extends RuntimeException {

    public ResponseTimeoutException(UUID uuid) {
        super("Response timed out for " + uuid.toString());
    }

}
