package su.windmill.bytes.util;

import java.util.Optional;
import java.util.function.Function;

public final class Either<A, B> {

    private static final Either<?, ?> EMPTY = new Either<>(null, null);

    private final A first;
    private final B second;

    private Either(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Either<A, B> empty() {
        return (Either<A, B>) EMPTY;
    }

    public static <A, B> Either<A, B> first(A first) {
        Assertions.notNull(first, "first");
        return new Either<>(first, null);
    }

    public static <A, B> Either<A, B> second(B second) {
        Assertions.notNull(second, "second");
        return new Either<>(null, second);
    }

    public static <A, B> Either<A, B> both(A first, B second) {
        Assertions.notNull(first, "first");
        Assertions.notNull(second, "second");
        return new Either<>(first, second);
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public boolean isFirstPresent() {
        return first != null;
    }

    public boolean isSecondPresent() {
        return second != null;
    }

    public boolean isBothPresent() {
        return isFirstPresent() && isSecondPresent();
    }

    public A first() {
        if(!isFirstPresent()) throw new NullPointerException("first is not present! check isFirstPresent() first");
        return first;
    }

    public B second() {
        if(!isSecondPresent()) throw new NullPointerException("second is not present! check isSecondPresent() first");
        return second;
    }

    public <A2, B2> Either<A2, B2> map(Function<A, A2> firstMapper, Function<B, B2> secondMapper) {
        if(isBothPresent()) return Either.both(
                firstMapper.apply(first),
                secondMapper.apply(second)
        );
        else if (isFirstPresent()) return Either.first(
                firstMapper.apply(first)
        );
        else if (isSecondPresent()) return Either.second(
                secondMapper.apply(second)
        );
        return Either.empty();
    }

    public Optional<A> firstAsOptional() {
        return Optional.ofNullable(first);
    }

    public Optional<B> secondAsOptional() {
        return Optional.ofNullable(second);
    }

}
