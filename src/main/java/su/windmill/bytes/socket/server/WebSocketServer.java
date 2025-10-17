package su.windmill.bytes.socket.server;

import su.windmill.bytes.socket.MessageWriter;
import su.windmill.bytes.socket.WebSocket;
import su.windmill.bytes.socket.connection.WebSocketConnection;

import java.io.IOException;
import java.util.Collection;

/**
 * WebSocket server basic interface
 */
public interface WebSocketServer extends WebSocket {

    /**
     * Starts server
     * @throws IOException if an IO error thrown when starting server
     */
    void start() throws IOException;

    /**
     * @return is server still running
     */
    boolean isRunning();

    /**
     * Sends binary message to specific client
     * @param connection client
     * @param writer message writer
     */
    void send(WebSocketConnection connection, MessageWriter writer);

    /**
     * Sends text message to specific client
     * @param connection client
     * @param text text message
     */
    void sendText(WebSocketConnection connection, String text);

    /**
     * Stops server
     * @throws IOException if an IO error thrown when stopping server
     */
    void stop() throws IOException;

    /**
     * Collection containing all connected clients
     */
    Collection<WebSocketConnection> clients();

}
