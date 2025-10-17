package su.windmill.bytes.socket.client;

import su.windmill.bytes.FastBytes;
import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.socket.Frame;
import su.windmill.bytes.socket.connection.WebSocketConnection;
import su.windmill.bytes.socket.connection.handshake.ClientHandshake;
import su.windmill.bytes.socket.exception.ListenerCallException;
import su.windmill.bytes.socket.listener.context.*;
import su.windmill.bytes.util.Assertions;
import su.windmill.bytes.util.Key;
import su.windmill.bytes.socket.ListenerService;
import su.windmill.bytes.socket.MessageWriter;
import su.windmill.bytes.socket.listener.Listener;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractWebSocketClient implements WebSocketClient {

    private final URI uri;

    private final ExecutorService executorService;
    private final ListenerService listenerService;

    private WebSocketConnection connection;
    private OutputStream outputStream;

    public AbstractWebSocketClient(URI uri) {
        this(
                uri,
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()),
                new ListenerService()
        );
    }

    public AbstractWebSocketClient(URI uri, ExecutorService executorService, ListenerService listenerService) {
        Assertions.notNull(uri, "uri");
        Assertions.notNull(executorService, "executorService");
        Assertions.notNull(listenerService, "listenerService");
        this.uri = uri;
        this.executorService = executorService;
        this.listenerService = listenerService;
    }

    @Override
    public void connect() throws IOException {
        String host = uri.getHost();
        int port = uri.getPort() == -1 ? (uri.getScheme().equals("wss") ? 443 : 80) : uri.getPort();
        Socket socket = new Socket(host, port);
        outputStream = socket.getOutputStream();

        connection = new WebSocketConnection(
                socket,
                false,
                new ClientHandshake(uri, host, port),
                (_, message) -> message(message.firstAsOptional().orElseGet(FastBytes::expanding), message.second()),
                (_, error) -> error(error),
                (_, code, reason) -> listenerService.call(ContextType.CLOSE, new CloseContext(this, code, reason))
        );
        connection.performHandshake();

        listenerService.call(ContextType.OPEN, new WebSocketContext(this));
        connection.startReadLoop(executorService);
    }

    protected void error(Throwable throwable) {
        if(throwable instanceof ListenerCallException) return;
        listenerService.call(ContextType.ERROR, new ErrorContext(this, throwable));
    }

    private void message(FastBuffer message, String textMessage) {
        listenerService.call(ContextType.MESSAGE, new MessageContext(this, message, Optional.ofNullable(textMessage)));
    }

    @Override
    public boolean isConnected() {
        return connection.connected();
    }

    @Override
    public void send(MessageWriter writer) {
        connection.send(writer);
    }

    @Override
    public void sendText(String text) {
        connection.sendText(text);
    }

    @Override
    public InetSocketAddress remoteSocketAddress() {
        return (InetSocketAddress) connection.socket().getRemoteSocketAddress();
    }

    @Override
    public InetSocketAddress localSocketAddress() {
        return (InetSocketAddress) connection.socket().getLocalSocketAddress();
    }

    @Override
    public void ping() {
        connection.writeFrame(
                Frame.OPCODE_PING,
                true,
                new byte[0]
        );
    }

    @Override
    public void close() {
        this.close(CLOSE_NORMAL, Optional.empty());
    }

    @Override
    public void close(int code, Optional<String> reason) {
        connection.close(code, reason.orElse(null));
    }

    @Override
    public <C extends ListenerContext, L extends Listener<C>> void addListener(Key key, ContextType<C> type, L listener) {
        listenerService.addListener(key, type, listener);
    }

    @Override
    public void removeListener(Key key, ContextType<?> type) {
        listenerService.removeListener(key, type);
    }

}
