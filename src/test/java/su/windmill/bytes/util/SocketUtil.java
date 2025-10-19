package su.windmill.bytes.util;

import java.io.IOException;
import java.net.ServerSocket;

public class SocketUtil {

    public static int getAvailablePort() throws InterruptedException {
        while (true) {
            try (ServerSocket serverSocket = new ServerSocket(0)) {
                serverSocket.setReuseAddress(true);
                return serverSocket.getLocalPort();
            }
            catch (IOException e) {
            }
            Thread.sleep(10);
        }
    }

}
