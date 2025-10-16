package su.windmill.bytes.socket.server;

import su.windmill.bytes.socket.MessageWriter;
import su.windmill.bytes.socket.WebSocket;
import su.windmill.bytes.socket.connection.WebSocketConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;

public interface WebSocketServer extends WebSocket {

    void start() throws IOException;

    boolean isRunning();

    void send(WebSocketConnection connection, MessageWriter writer);

    void sendText(WebSocketConnection connection, String text);

    void stop() throws IOException;

    Collection<WebSocketConnection> clients();

}
