package su.windmill.bytes.socket.server;

import su.windmill.bytes.socket.WebSocket;

import java.io.IOException;
import java.net.InetSocketAddress;

public interface WebSocketServer extends WebSocket {

    void start(InetSocketAddress address) throws IOException;

    void stop() throws IOException;

    boolean isRunning();

}
