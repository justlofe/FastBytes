package su.windmill.bytes.util;

public class Assertions {

    public static void notNull(Object object, String name) {
        if(object == null) throw new NullPointerException(name + " is null");
    }

    public static <N extends Comparable<N>> void between(N value, N min, N max) {
        if(max.compareTo(min) < 1) throw new IllegalArgumentException("max should be bigger than min");
        if(value.compareTo(min) < 0 || value.compareTo(max) > 0) throw new IllegalArgumentException("value is not between min and max");
    }

}
