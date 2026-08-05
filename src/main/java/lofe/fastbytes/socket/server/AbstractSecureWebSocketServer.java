package lofe.fastbytes.socket.server;

import lofe.fastbytes.socket.connection.WebSocketConnection;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class AbstractSecureWebSocketServer extends AbstractWebSocketServer {

    private final SSLContext sslContext;

    public AbstractSecureWebSocketServer(InetSocketAddress socketAddress, ServerHandler serverHandler, SSLContext sslContext) {
        super(socketAddress, serverHandler);
        this.sslContext = sslContext;
    }

    @Override
    public void start() throws IOException {
        if(running) return;

        SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
        SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(
                socketAddress.getPort(),
                0,
                socketAddress.getAddress()
        );
        this.serverSocket = serverSocket;

        serverSocket.setEnabledProtocols(new String[]{
                "TLSv1.3",
                "TLSv1.2"
        });
        serverSocket.setEnabledCipherSuites(new String[]{
                "TLS_AES_256_GCM_SHA384",
                "TLS_AES_128_GCM_SHA256",
                "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384",
                "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"
        });

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

    @Override
    protected WebSocketConnection createConnection(Socket socket) throws IOException {
        return super.createConnection(socket);
    }
}
