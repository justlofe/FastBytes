package su.windmill.bytes.socket.server;

import su.windmill.bytes.FastBytes;
import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.socket.ListenerService;
import su.windmill.bytes.socket.MessageWriter;
import su.windmill.bytes.socket.connection.WebSocketConnection;
import su.windmill.bytes.socket.connection.handshake.ServerHandshake;
import su.windmill.bytes.socket.exception.ListenerCallException;
import su.windmill.bytes.socket.listener.Listener;
import su.windmill.bytes.socket.listener.context.*;
import su.windmill.bytes.util.Key;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public abstract class AbstractWebSocketServer implements WebSocketServer {

    private final InetSocketAddress socketAddress;

    private final ListenerService listenerService;

    private final Set<WebSocketConnection> CONNECTIONS = Collections.synchronizedSet(new HashSet<>());

    private volatile Thread serverThread;
    private volatile boolean running;
    private volatile ServerSocket serverSocket;

    public AbstractWebSocketServer(InetSocketAddress socketAddress) {
        this(
                socketAddress,
                new ListenerService()
        );
    }

    public AbstractWebSocketServer(InetSocketAddress socketAddress, ListenerService listenerService) {
        this.socketAddress = socketAddress;
        this.listenerService = listenerService;
    }

    public Thread serverThread() {
        return serverThread;
    }

    public ServerSocket serverSocket() {
        return serverSocket;
    }

    @Override
    public void start() throws IOException {
        if(running) return;

        serverSocket = new ServerSocket(socketAddress.getPort(), 0);
        running = true;

        listenerService.call(ContextType.START, new WebSocketContext(this));

        serverThread = Thread.startVirtualThread(() -> {
            while (running) {
                WebSocketConnection connection = null;
                try {
                    Socket socket = serverSocket.accept();
                    socket.setTcpNoDelay(true);

                    connection = createConnection(socket);
                    connection.performHandshake();

                    CONNECTIONS.add(connection);
                    connection.startReadLoop();

                    listenerService.call(ContextType.SERVER_OPEN, new ServerOpenContext(this, connection));
                }
                catch (Throwable throwable) {
                    if(connection != null) connection.shutdown(true);
                }
            }
        });
    }

    protected void error(Throwable throwable) {
        if(throwable instanceof ListenerCallException) return;
        listenerService.call(ContextType.ERROR, new ErrorContext(this, throwable));
    }

    private WebSocketConnection createConnection(Socket socket) throws IOException {
        return new WebSocketConnection(
                socket,
                true,
                ServerHandshake.INSTANCE,
                (connection, message) -> message(connection, message.firstAsOptional().orElseGet(FastBytes::expanding), message.second()),
                (_, throwable) -> listenerService.call(ContextType.ERROR, new ErrorContext(this, throwable)),
                (connection, code, reason) -> {
                    listenerService.call(ContextType.SERVER_CLOSE, new ServerCloseContext(
                            this,
                            code,
                            reason,
                            connection
                    ));
                    CONNECTIONS.remove(connection);
                }
        );
    }

    private void message(WebSocketConnection connection, FastBuffer message, String textMessage) {
        listenerService.call(ContextType.SERVER_MESSAGE, new ServerMessageContext(
                this,
                message,
                Optional.ofNullable(textMessage),
                connection
        ));
    }

    @Override
    public void stop() throws IOException {
        if(!running) return;
        running = false;

        serverSocket.close();

        synchronized (CONNECTIONS) {
            CONNECTIONS.forEach(WebSocketConnection::shutdown);
            CONNECTIONS.clear();
        }
    }

    @Override
    public Collection<WebSocketConnection> clients() {
        return CONNECTIONS;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void send(WebSocketConnection connection, MessageWriter writer) {
        connection.send(writer);
    }

    @Override
    public void sendText(WebSocketConnection connection, String text) {
        connection.sendText(text);
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
