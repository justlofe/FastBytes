package su.windmill.bytes.socket.client;

import java.net.URI;

public interface TestClient {

    void testConnect() throws Exception;

    void testClose() throws Exception;

    void testSend(String message);

    boolean testActive();

    static TestClient fastBytes(URI uri, String expectedResponse) {
        return new FastBytesTestClient(uri, expectedResponse);
    }

}
