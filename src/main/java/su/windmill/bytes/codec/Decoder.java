package su.windmill.bytes.codec;

import su.windmill.bytes.codec.context.DecodeContext;

@FunctionalInterface
public interface Decoder<E> {

    E decode(DecodeContext ctx);

}
