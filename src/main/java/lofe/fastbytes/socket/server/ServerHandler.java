package lofe.fastbytes.socket.server;

import lofe.fastbytes.buffer.FastBuffer;
import lofe.fastbytes.socket.connection.WebSocketConnection;

public interface ServerHandler {

    default void start(WebSocketServer server) {}

    default void open(WebSocketServer server, WebSocketConnection client) {}

    default void message(WebSocketServer server, WebSocketConnection client, FastBuffer message, String textMessage) {}

    default void close(WebSocketServer server, WebSocketConnection client, int code, String reason) {}

    default void clientError(WebSocketServer server, WebSocketConnection client, Throwable throwable) {}


}
