# FastBytes
Simple WebSocket's library with some additions.

> [!CAUTION]
> Don't use this library in production! It's mainly written just as proof of concept, so can include many security issues. Better choose something like [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket)

## Features
- WebSocket - basic implementation for client and server
- FastBuffer - allowing to read and write bytes
- Codec - allows to encode and decode objects you want, write own codecs - use it

# Usage

### Gradle
```kotlin
repositories {
    mavenCentral()
    maven {url = uri("https://jitpack.io")}
}

dependencies {
    implementation("com.github.justlofe:FastBytes:1.0")
}
```

## Examples
For example, we will create a class and codec for him.

## WebSocket's
<details>
<summary>Client implementation</summary>

```java
import su.windmill.bytes.socket.client.AbstractWebSocketClient;
import su.windmill.bytes.socket.listener.context.ContextType;
import su.windmill.bytes.util.Key;

import java.io.IOException;
import java.net.URI;

public class ExampleClient extends AbstractWebSocketClient {

    public ExampleClient(URI uri) {
        super(uri);

        addListener(Key.key("open"), ContextType.OPEN, _ -> System.out.println("Connected"));
        addListener(Key.key("close"), ContextType.CLOSE, context -> {
            System.out.printf(
                    "Closed connection. [Code: %s, Reason: \"%s\"]\n",
                    context.code(),
                    context.reason().orElse("no reason")
            );
        });
        addListener(Key.key("message"), ContextType.MESSAGE, context -> {
            String message = context.textMessage().orElse("binary");
            System.out.println("Received message from server: [Message: \"" + message + "\"]");
        });
        addListener(Key.key("error"), ContextType.ERROR, context -> System.err.println("Exception thrown: " + context.throwable().getMessage()));
    }

    public static void main(String[] args) throws IOException {
        ExampleClient client = new ExampleClient(URI.create("wss://localhost:433"));
        client.connect();
        
        client.sendText("Ping!");
    }

}
```
</details>

<details>
<summary>Server implementation</summary>

```java
import su.windmill.bytes.socket.listener.context.ContextType;
import su.windmill.bytes.util.Key;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ExampleServer extends AbstractWebSocketServer {

    public ExampleServer(int port) {
        super(new InetSocketAddress(port));

        addListener(Key.key("open"), ContextType.SERVER_OPEN, _ -> System.out.println("Connection opened"));
        addListener(Key.key("close"), ContextType.SERVER_CLOSE, context -> {
            System.out.printf(
                    "Closed connection. [Code: %s, Reason: \"%s\"]%n",
                    context.code(),
                    context.reason().orElse("no reason")
            );
        });
        addListener(Key.key("message"), ContextType.SERVER_MESSAGE, context -> {
            String message = context.textMessage().orElse("binary");
            System.out.println("Got a message from client: [Message: \"" + message + "\"]");
        });
        addListener(Key.key("error"), ContextType.ERROR, context -> System.err.println("Exception thrown: " + context.throwable().getMessage()));
        addListener(Key.key("start"), ContextType.START, _ -> System.out.println("Started"));
    }

    public static void main(String[] args) throws IOException {
        ExampleServer server = new ExampleServer(433);
        server.start();
    }

}
```

</details>

## Codec's

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
import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.FastBytes;
import su.windmill.bytes.codec.Codec;
import su.windmill.bytes.codec.context.DecodeContext;

import java.util.UUID;

public class Main {

    public static final Codec<ExampleEncodable> EXAMPLE_CODEC = Codec.fixed(
            (encodable, buffer) -> {
                // Encoding id
                buffer.writeLong(encodable.id.getMostSignificantBits());
                buffer.writeLong(encodable.id.getLeastSignificantBits());

                // Encoding name
                buffer.writeUTF8(encodable.name);

                // Encoding someParameter
                buffer.writeInt(encodable.someParameter);
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
                int someParameter = buffer.readInt();

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
