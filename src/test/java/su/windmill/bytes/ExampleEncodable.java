package su.windmill.bytes;

import java.util.Objects;
import java.util.Random;
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

    public static ExampleEncodable randomized() {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        int stringSize = random.nextInt(64);
        for (int i = 0; i < stringSize; i++) {
            builder.append((char) (short) random.nextInt(256));
        }

        return new ExampleEncodable(
                UUID.randomUUID(),
                builder.toString(),
                random.nextInt(Integer.MAX_VALUE - 1)
        );
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ExampleEncodable that)) return false;
        return id.equals(that.id)
                && name.equals(that.name)
                && someParameter == that.someParameter;
    }

    @Override
    public String toString() {
        return "ExampleEncodable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", someParameter=" + someParameter +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, someParameter);
    }
}