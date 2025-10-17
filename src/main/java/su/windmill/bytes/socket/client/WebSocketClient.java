package su.windmill.bytes.socket.client;

import su.windmill.bytes.socket.MessageWriter;
import su.windmill.bytes.socket.WebSocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * WebSocket client basic interface
 */
public interface WebSocketClient extends WebSocket {

    /**
     * Creates connection to a server
     * @throws IOException if an IO error thrown when connecting
     */
    void connect() throws IOException;

    /**
     * @return is still connected to server
     */
    boolean isConnected();

    /**
     * Sends binary message to server
     * @param writer message writer
     */
    void send(MessageWriter writer);

    /**
     * Sends text message to server
     * @param text text message
     */
    void sendText(String text);

    /**
     * @return address of server client connected to
     */
    InetSocketAddress remoteSocketAddress();

    /**
     * @return address client bound to
     */
    InetSocketAddress localSocketAddress();

    /**
     * Sends ping to a server
     */
    void ping(); // pings server

    /**
     * Closes connection with WebSocket.CLOSE_NORMAL (1000) code
     */
    void close();

    /**
     * Closes connection with specific code and reason
     * @param code websocket close code
     * @param reason reason to close
     */
    void close(int code, Optional<String> reason);

}
