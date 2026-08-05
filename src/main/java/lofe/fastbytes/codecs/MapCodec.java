package lofe.fastbytes.codecs;

import lofe.fastbytes.buffer.FastBuffer;
import lofe.fastbytes.codec.StreamCodec;
import lofe.fastbytes.codec.context.DecodeContext;

import java.util.HashMap;
import java.util.Map;

public class MapCodec<K, V> implements StreamCodec<Map<K, V>> {

    private final StreamCodec<K> keyStreamCodec;
    private final StreamCodec<V> valueStreamCodec;

    public MapCodec(StreamCodec<K> keyStreamCodec, StreamCodec<V> valueStreamCodec) {
        this.keyStreamCodec = keyStreamCodec;
        this.valueStreamCodec = valueStreamCodec;
    }

    @Override
    public Map<K, V> decode(DecodeContext ctx) {
        FastBuffer buf = ctx.buffer();
        int count = buf.readInt();
        Map<K, V> map = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            map.put(
                    keyStreamCodec.decode(ctx),
                    valueStreamCodec.decode(ctx)
            );
        }
        return map;
    }

    @Override
    public void encode(Map<K, V> encodable, FastBuffer buffer) {
        buffer.writeInt(encodable.size());
        encodable.forEach((key, value) -> {
            keyStreamCodec.encode(key, buffer);
            valueStreamCodec.encode(value, buffer);
        });
    }

}
