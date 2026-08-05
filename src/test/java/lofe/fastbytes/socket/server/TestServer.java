package lofe.fastbytes.socket.server;

import lofe.fastbytes.util.SocketUtil;

public interface TestServer {

    void testStart() throws Exception;

    void testStop() throws Exception;

    int port();

    static TestServer thirdParty(String message, String response) throws Exception {
        return new ThirdPartyTestServer(SocketUtil.getAvailablePort(), message, response);
    }

    static TestServer fastBytes(String message, String response) throws Exception {
        return new FastBytesTestServer(SocketUtil.getAvailablePort(), message, response);
    }

}
