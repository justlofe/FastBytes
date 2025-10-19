package su.windmill.bytes.util;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Key {

    private static final Pattern FORMAT = Pattern.compile("^[a-z0-9_-]+$");

    private final String value;

    private Key(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static Key key(String value) {
        if(!FORMAT.matcher(value).find()) throw new IllegalArgumentException("Value string is not allowed. Allowed format is: ^[a-z0-9_-]+$");
        return new Key( value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
