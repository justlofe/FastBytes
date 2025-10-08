package su.windmill.bytes.codecs;

import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.codec.Codec;
import su.windmill.bytes.codec.context.DecodeContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StreamCodec<E> implements Codec<Collection<E>> {

    private final Codec<E> codec;

    public StreamCodec(Codec<E> codec) {
        this.codec = codec;
    }

    @Override
    public Collection<E> decode(DecodeContext ctx) {
        FastBuffer buf = ctx.buffer();
        int count = buf.readInt();
        List<E> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(codec.decode(ctx));
        }
        return list;
    }

    @Override
    public void encode(Collection<E> encodable, FastBuffer buffer) {
        buffer.writeInt(encodable.size());
        encodable.forEach(element -> codec.encode(element, buffer));
    }

}
