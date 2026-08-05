package lofe.fastbytes.socket.client;

import lofe.fastbytes.socket.CloseCode;
import lofe.fastbytes.socket.Frame;
import lofe.fastbytes.socket.MessageWriter;
import lofe.fastbytes.socket.connection.WebSocketConnection;
import lofe.fastbytes.socket.connection.handshake.ClientHandshake;
import lofe.fastbytes.socket.exception.HandlerException;
import lofe.fastbytes.util.Assertions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractWebSocketClient implements WebSocketClient {

    private final URI uri;
    private ClientHandler clientHandler;

    private WebSocketConnection connection;

    public AbstractWebSocketClient(URI uri) {
        this(uri, null);
    }

    public AbstractWebSocketClient(URI uri, ClientHandler clientHandler) {
        Assertions.notNull(uri, "uri");
        this.uri = uri;
        this.clientHandler = clientHandler;
    }

    public void clientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    private void callToHandler(Consumer<ClientHandler> consumer) {
        if(clientHandler == null) return;
        try {
            consumer.accept(clientHandler);
        }
        catch (Throwable throwable) {
            throw new HandlerException(throwable);
        }
    }

    @Override
    public void connect() throws IOException {
        if(isConnected()) return;
        String host = uri.getHost();
        int port = uri.getPort() == -1 ? (uri.getScheme().equals("wss") ? 443 : 80) : uri.getPort();
        Socket socket = new Socket(host, port);

        connection = new WebSocketConnection(
                socket,
                false,
                new ClientHandshake(uri, host, port),
                (_, message) -> callToHandler(clientHandler -> clientHandler.message(
                        this,
                        message.firstAsOptional().orElse(null),
                        message.secondAsOptional().orElse(null)
                )),
                (_, error) -> error(error),
                (_, code, reason) -> callToHandler(clientHandler -> clientHandler.close(this, code, reason))
        );
        connection.performHandshake();

        callToHandler(clientHandler -> clientHandler.open(this));
        connection.startReadLoop();
    }

    protected void error(Throwable throwable) {
        if(throwable instanceof HandlerException) return;
        callToHandler(clientHandler -> clientHandler.error(this, throwable));
    }

    @Override
    public boolean isConnected() {
        return connection != null && connection.connected();
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
        if(!isConnected()) return;
        this.close(CloseCode.CLOSE_NORMAL.code, Optional.empty());
    }

    @Override
    public void close(int code, Optional<String> reason) {
        connection.close(code, reason.orElse(null));
    }

}
