package su.windmill.bytes.codecs;

import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.codec.Codec;
import su.windmill.bytes.codec.context.DecodeContext;

import java.util.HashMap;
import java.util.Map;

public class MapCodec<K, V> implements Codec<Map<K, V>> {

    private final Codec<K> keyCodec;
    private final Codec<V> valueCodec;

    public MapCodec(Codec<K> keyCodec, Codec<V> valueCodec) {
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
    }

    @Override
    public Map<K, V> decode(DecodeContext ctx) {
        FastBuffer buf = ctx.buffer();
        int count = buf.readInt();
        Map<K, V> map = new HashMap<>(count);
        for (int i = 0; i < count; i++) {
            map.put(
                    keyCodec.decode(ctx),
                    valueCodec.decode(ctx)
            );
        }
        return map;
    }

    @Override
    public void encode(Map<K, V> encodable, FastBuffer buffer) {
        buffer.writeInt(encodable.size());
        encodable.forEach((key, value) -> {
            keyCodec.encode(key, buffer);
            valueCodec.encode(value, buffer);
        });
    }

}
