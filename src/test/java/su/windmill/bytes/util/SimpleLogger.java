package su.windmill.bytes.util;

import java.util.Optional;

public final class SimpleLogger {

    private final String name;

    public SimpleLogger(Object loggable) {
        this(loggable == null ? "null" : loggable.getClass().getSimpleName());
    }

    public SimpleLogger(String name) {
        Assertions.notNull(name, "name");
        this.name = name;
    }

    public void info(String message) {
        log(Type.INFO, message);
    }

    public void warn(String message) {
        log(Type.WARN, message);
    }

    public void error(String message) {
        log(Type.ERROR, message);
    }

    private void log(Type type, String message) {
        if(type.ansiColor.isEmpty()) {
            System.out.printf("[%s] %s\n", name, message);
            return;
        }

        System.out.printf("%s[%s] %s\u001B[0m\n", type.ansiColor.get(), name, message);
    }

    private enum Type {
        INFO,
        WARN(Optional.of("\u001B[33m")),
        ERROR(Optional.of("\u001B[31m"));

        public final Optional<String> ansiColor;

        Type() {
            this(Optional.empty());
        }

        Type(Optional<String> ansiColor) {
            this.ansiColor = ansiColor;
        }
    }

}
