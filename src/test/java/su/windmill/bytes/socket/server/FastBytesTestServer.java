package su.windmill.bytes.socket.server;

import su.windmill.bytes.socket.listener.context.ContextType;
import su.windmill.bytes.util.Key;
import su.windmill.bytes.util.SimpleLogger;

import java.net.InetSocketAddress;

class FastBytesTestServer extends AbstractWebSocketServer implements TestServer {

    private final SimpleLogger logger = new SimpleLogger(this);

    private final int port;
    private final String message, response;

    public FastBytesTestServer(int port, String message, String response) {
        super(new InetSocketAddress(port));
        this.port = port;
        this.message = message;
        this.response = response;
    }

    @Override
    public void testStart() throws Exception {
        addListener(Key.key("open"), ContextType.SERVER_OPEN, _ -> logger.info("Connection opened"));
        addListener(Key.key("close"), ContextType.SERVER_CLOSE, context -> {
            logger.info(String.format(
                    "Closed connection. [Code: %s, Reason: \"%s\"]",
                    context.code(),
                    context.reason().orElse("no reason")
            ));
        });
        addListener(Key.key("message"), ContextType.SERVER_MESSAGE, context -> {
            String message = context.textMessage().orElse("binary");
            logger.info("Got a message from client: [Message: \"" + message + "\"]");

            if(this.message.equals(message)) {
                sendText(context.client(), this.response);
            }
        });
        addListener(Key.key("error"), ContextType.ERROR, context -> {
            logger.warn("Exception thrown: " + context.throwable().getMessage());
        });
        addListener(Key.key("start"), ContextType.START, _ -> logger.info("Started"));

        start();
    }

    @Override
    public void testStop() throws Exception {
        stop();
    }

    @Override
    public int port() {
        return port;
    }

}
