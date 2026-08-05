package lofe.fastbytes.codec;

import lofe.fastbytes.codec.context.DecodeContext;

@FunctionalInterface
public interface StreamDecoder<E> {

    E decode(DecodeContext ctx);

}
