package lofe.fastbytes.codecs;

import lofe.fastbytes.buffer.FastBuffer;
import lofe.fastbytes.codec.StreamCodec;
import lofe.fastbytes.codec.context.DecodeContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionCodec<E> implements StreamCodec<Collection<E>> {

    private final StreamCodec<E> streamCodec;

    public CollectionCodec(StreamCodec<E> streamCodec) {
        this.streamCodec = streamCodec;
    }

    @Override
    public Collection<E> decode(DecodeContext ctx) {
        FastBuffer buf = ctx.buffer();
        int count = buf.readInt();
        List<E> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(streamCodec.decode(ctx));
        }
        return list;
    }

    @Override
    public void encode(Collection<E> encodable, FastBuffer buffer) {
        buffer.writeInt(encodable.size());
        encodable.forEach(element -> streamCodec.encode(element, buffer));
    }

}
