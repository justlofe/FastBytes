# FastBytes
Simple library for work with bytes like ByteBuf.

## Features
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
