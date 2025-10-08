package su.windmill.bytes.examples;

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