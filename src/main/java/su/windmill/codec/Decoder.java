package su.windmill.codec;

import su.windmill.codec.context.DecodeContext;

@FunctionalInterface
public interface Decoder<E> {

    E decode(DecodeContext ctx);

}
