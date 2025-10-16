package su.windmill.bytes.socket.server;

import su.windmill.bytes.FastBytes;
import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.socket.ListenerService;
import su.windmill.bytes.socket.MessageWriter;
import su.windmill.bytes.socket.connection.WebSocketConnection;
import su.windmill.bytes.socket.connection.handshake.ServerHandshake;
import su.windmill.bytes.socket.listener.Listener;
import su.windmill.bytes.socket.listener.context.ContextType;
import su.windmill.bytes.socket.listener.context.ListenerContext;
import su.windmill.bytes.socket.listener.context.ServerCloseContext;
import su.windmill.bytes.socket.listener.context.ServerMessageContext;
import su.windmill.bytes.util.Key;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractWebSocketServer implements WebSocketServer {

    private final InetSocketAddress socketAddress;

    private final ExecutorService executorService;
    private final ListenerService listenerService;

    private final Set<WebSocketConnection> CONNECTIONS = Collections.synchronizedSet(new HashSet<>());

    private volatile boolean running;
    private ServerSocket serverSocket;

    public AbstractWebSocketServer(InetSocketAddress socketAddress) {
        this(
                socketAddress,
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()),
                new ListenerService()
        );
    }

    public AbstractWebSocketServer(InetSocketAddress socketAddress, ExecutorService executorService, ListenerService listenerService) {
        this.socketAddress = socketAddress;
        this.executorService = executorService;
        this.listenerService = listenerService;
    }

    @Override
    public void start() throws IOException {
        if(running) return;

        serverSocket = new ServerSocket(socketAddress.getPort(), 0);
        running = true;

        executorService.execute(() -> {
            while (running) {
                try {
                    Socket socket = serverSocket.accept();
                    socket.setTcpNoDelay(true);

                    WebSocketConnection connection = createConnection(socket);
                    connection.performHandshake();

                    CONNECTIONS.add(connection);
                    connection.startReadLoop(executorService);
                }
                catch (Throwable throwable) {
                    try {
                        serverSocket.close();
                    }
                    catch (Throwable _) {}
                }
            }
        });
    }

    private WebSocketConnection createConnection(Socket socket) throws IOException {
        Reference<WebSocketConnection> reference = new WeakReference<>(null);
        WebSocketConnection connection = new WebSocketConnection(
                socket,
                true,
                ServerHandshake.INSTANCE,
                either -> message(reference.get(), either.firstAsOptional().orElseGet(FastBytes::expanding), either.second()),
                throwable -> {
                    throwable.printStackTrace();
                },
                (code, reason) -> listenerService.call(ContextType.SERVER_CLOSE, new ServerCloseContext(
                        this,
                        code,
                        reason,
                        reference.get()
                ))
        );
        reference.refersTo(connection);
        return connection;
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
        executorService.shutdownNow();

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
