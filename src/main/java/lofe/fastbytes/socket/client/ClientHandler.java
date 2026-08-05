package lofe.fastbytes.socket.client;

import lofe.fastbytes.buffer.FastBuffer;

public interface ClientHandler {

    default void open(WebSocketClient client) {}

    default void close(WebSocketClient client, int code, String reason) {}

    default void message(WebSocketClient client, FastBuffer message, String textMessage) {}

    default void error(WebSocketClient client, Throwable throwable) {}

}
