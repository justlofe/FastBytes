package su.windmill.bytes.socket.client;

import su.windmill.bytes.socket.MessageWriter;
import su.windmill.bytes.socket.WebSocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;

public interface WebSocketClient extends WebSocket {

    void connect() throws IOException;

    boolean isConnected();

    void send(MessageWriter writer);

    void sendText(String text);

    InetSocketAddress remoteSocketAddress();
    InetSocketAddress localSocketAddress();

    void ping(); // pings server

    void close();

    void close(int code, Optional<String> reason);

}
