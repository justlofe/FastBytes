package lofe.fastbytes.socket.server;

import lofe.fastbytes.socket.MessageWriter;
import lofe.fastbytes.socket.connection.WebSocketConnection;
import lofe.fastbytes.socket.connection.handshake.ServerHandshake;
import lofe.fastbytes.socket.exception.HandlerException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.function.Consumer;

public class AbstractWebSocketServer implements WebSocketServer {

    protected final InetSocketAddress socketAddress;
    protected ServerHandler serverHandler;

    protected final Set<WebSocketConnection> CONNECTIONS = Collections.synchronizedSet(new HashSet<>());

    protected volatile Thread serverThread;
    protected volatile boolean running;
    protected volatile ServerSocket serverSocket;

    public AbstractWebSocketServer(InetSocketAddress socketAddress) {
        this(socketAddress, null);
    }

    public AbstractWebSocketServer(InetSocketAddress socketAddress, ServerHandler serverHandler) {
        this.socketAddress = socketAddress;
        this.serverHandler = serverHandler;
    }

    public void serverHandler(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public Thread serverThread() {
        return serverThread;
    }

    public ServerSocket serverSocket() {
        return serverSocket;
    }

    protected void callToHandler(Consumer<ServerHandler> consumer) {
        if(serverHandler == null) return;
        try {
            consumer.accept(serverHandler);
        }
        catch (Throwable throwable) {
            throw new HandlerException(throwable);
        }
    }

    @Override
    public void start() throws IOException {
        if(running) return;

        serverSocket = new ServerSocket(socketAddress.getPort(), 0);
        running = true;

        callToHandler(serverHandler -> serverHandler.start(this));

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

                    final WebSocketConnection finalConnection = connection;
                    callToHandler(serverHandler -> serverHandler.open(this, finalConnection));
                }
                catch (Throwable throwable) {
                    if(connection != null) connection.shutdown(true);
                }
            }
        });
    }

    protected void error(WebSocketConnection client, Throwable throwable) {
        if(throwable instanceof HandlerException) return;
        callToHandler(serverHandler -> serverHandler.clientError(this, client, throwable));
    }

    protected WebSocketConnection createConnection(Socket socket) throws IOException {
        return new WebSocketConnection(
                socket,
                true,
                ServerHandshake.INSTANCE,
                (connection, message) -> callToHandler(serverHandler -> serverHandler.message(
                        this,
                        connection,
                        message.firstAsOptional().orElse(null),
                        message.secondAsOptional().orElse(null)
                )),
                this::error,
                (connection, code, reason) -> {
                    callToHandler(serverHandler -> serverHandler.close(this, connection, code, reason));
                    CONNECTIONS.remove(connection);
                }
        );
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

}
