package lofe.fastbytes;

import lofe.fastbytes.codec.StreamCodec;
import lofe.fastbytes.codecs.CollectionCodec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import lofe.fastbytes.buffer.FastBuffer;
import lofe.fastbytes.codec.context.DecodeContext;

import java.util.List;

public class CollectionCodecTest {

    public static final CollectionCodec<ExampleEncodable> ENCODABLE_COLLECTION_CODEC = StreamCodec.stream(ExampleTest.ENCODABLE_STREAM_CODEC);

    @Test
    public void test() {
        List<ExampleEncodable> list = List.of(
                ExampleEncodable.randomized(),
                ExampleEncodable.randomized(),
                ExampleEncodable.randomized()
        );

        try (FastBuffer buffer = FastBytes.expanding()) {
            ENCODABLE_COLLECTION_CODEC.encode(list, buffer);

            List<ExampleEncodable> list2 = List.copyOf(ENCODABLE_COLLECTION_CODEC.decode(DecodeContext.of(buffer)));
            int size = list2.size();
            Assertions.assertEquals(list.size(), size);

            for (int i = 0; i < size; i++) {
                Assertions.assertEquals(list.get(i), list2.get(i));
            }
        }
    }

}
