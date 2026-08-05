# FastBytes
Simple WebSocket's library with some additions.

> [!CAUTION]
> Please, do not use this library in production! It's mainly written for educational purposes, so can include many security issues. Better choose something like [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket)

## Features
- WebSocket - basic implementations for client and server
- FastBuffer - buffers for reading and writing raw data
- StreamCodecs - allows to encode and decode objects you want

# Usage

### Gradle
```kotlin
repositories {
    mavenCentral()
    maven {url = uri("https://repository.modoru.fun/releases")}
}

dependencies {
    implementation("lofe.fastbytes:fastbytes:1.3")
}
```

## Examples

## WebSockets
<details>
<summary>Client implementation</summary>

```java
import lofe.fastbytes.buffer.FastBuffer;
import lofe.fastbytes.socket.client.AbstractWebSocketClient;
import lofe.fastbytes.socket.client.ClientHandler;
import lofe.fastbytes.socket.client.WebSocketClient;

import java.io.IOException;
import java.net.URI;

public final class ExampleClient extends AbstractWebSocketClient implements ClientHandler {

    public ExampleClient(URI uri) {
        super(uri);
    }

    @Override
    public void open(WebSocketClient client) {
        System.out.println("Connected");
    }

    @Override
    public void close(WebSocketClient client, int code, String reason) {
        System.out.printf("Closed connection. [Code: %s, Reason: \"%s\"]\n", code, reason);
    }

    @Override
    public void message(WebSocketClient client, FastBuffer message, String textMessage) {
        System.out.println("Received message from server: [Message: \"" + (message == null ? textMessage : "binary") + "\"]");
    }

    @Override
    public void error(WebSocketClient client, Throwable throwable) {
        System.err.println("Exception thrown: " + throwable.getMessage());
    }

    public static void main(String[] args) throws IOException {
        ExampleClient exampleClient = new ExampleClient(URI.create("wss://localhost:433"));
        exampleClient.clientHandler(exampleClient);
        exampleClient.connect();

        exampleClient.sendText("Ping!");
    }

}
```
</details>

<details>
<summary>Server implementation</summary>

```java
import lofe.fastbytes.buffer.FastBuffer;
import lofe.fastbytes.socket.connection.WebSocketConnection;
import lofe.fastbytes.socket.server.AbstractWebSocketServer;
import lofe.fastbytes.socket.server.ServerHandler;
import lofe.fastbytes.socket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public final class ExampleServer extends AbstractWebSocketServer implements ServerHandler {

    public ExampleServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void open(WebSocketServer server, WebSocketConnection client) {
        System.out.println("Connection opened");
    }

    @Override
    public void close(WebSocketServer server, WebSocketConnection client, int code, String reason) {
        System.out.printf(
                "Closed connection. [Code: %s, Reason: \"%s\"]%n",
                code,
                reason
        );
    }

    @Override
    public void message(WebSocketServer server, WebSocketConnection client, FastBuffer message, String textMessage) {
        System.out.println("Got a message from client: [Message: \"" + (message == null ? textMessage : "binary") + "\"]");
    }

    @Override
    protected void error(WebSocketConnection client, Throwable throwable) {
        System.err.println("Exception thrown: " + throwable.getMessage());
    }

    @Override
    public void start() {
        System.out.println("Started");
    }

    public static void main(String[] args) throws IOException {
        ExampleServer exampleServer = new ExampleServer(433);
        exampleServer.serverHandler(exampleServer);
        exampleServer.start();
    }

}
```

</details>

## Codec's
For example, we will create a class and a codec for it.

```java
// ExampleEncodable.java
import java.util.UUID;

public class ExampleEncodable {

    public final UUID id;
    public final String name;
    public int someParameter;
    
    public ExampleEncodable(UUID id, String name, int someParameter) {
        this.id = id;
        this.name = name;
        this.someParameter = someParameter;
    }

}

```

```java
// Main.java

import lofe.fastbytes.buffer.FastBuffer;
import lofe.fastbytes.FastBytes;
import lofe.fastbytes.codec.StreamCodec;
import lofe.fastbytes.codec.context.DecodeContext;

import java.util.UUID;

public class Main {

    public static final StreamCodec<ExampleEncodable> EXAMPLE_CODEC = StreamCodec.fixed(
            (encodable, buffer) -> {
                // Encoding id
                buffer.writeLong(encodable.id.getMostSignificantBits());
                buffer.writeLong(encodable.id.getLeastSignificantBits());

                // Encoding name
                buffer.writeUTF8(encodable.name);

                // Encoding someParameter
                buffer.writeVarInt(encodable.someParameter);
            },
            (ctx) -> {
                FastBuffer buffer = ctx.buffer();

                // Decoding id
                UUID id = new UUID(
                        buffer.readLong(),
                        buffer.readLong()
                );

                // Decoding name
                String name = buffer.readUTF8();

                // Decoding someParameter
                int someParameter = buffer.readVarInt();

                return new ExampleEncodable(id, name, someParameter);
            }
    );

    public static void main(String[] args) {
        ExampleEncodable exampleEncodable = new ExampleEncodable(
                UUID.randomUUID(),
                "A Name",
                42
        );

        FastBuffer buffer = FastBytes.expanding();

        // Encode to buffer
        EXAMPLE_CODEC.encode(exampleEncodable, buffer);

        // Decode from buffer
        ExampleEncodable decoded = EXAMPLE_CODEC.decode(DecodeContext.of(buffer));

        // Print data
        System.out.printf(
                "id: %s\nname: %s\nsomeParameter: %s\n",
                decoded.id,
                decoded.name,
                decoded.someParameter
        );
    }

}
```
