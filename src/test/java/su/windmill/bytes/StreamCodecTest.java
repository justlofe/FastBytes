package su.windmill.bytes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import su.windmill.bytes.buffer.FastBuffer;
import su.windmill.bytes.codec.Codec;
import su.windmill.bytes.codec.context.DecodeContext;
import su.windmill.bytes.codecs.StreamCodec;

import java.util.List;

public class StreamCodecTest {

    public static final StreamCodec<ExampleEncodable> ENCODABLE_STREAM_CODEC = Codec.stream(ExampleTest.ENCODABLE_CODEC);

    @Test
    public void test() {
        List<ExampleEncodable> list = List.of(
                ExampleEncodable.randomized(),
                ExampleEncodable.randomized(),
                ExampleEncodable.randomized()
        );

        FastBuffer buffer = FastBytes.expanding();
        ENCODABLE_STREAM_CODEC.encode(list, buffer);

        List<ExampleEncodable> list2 = List.copyOf(ENCODABLE_STREAM_CODEC.decode(DecodeContext.of(buffer)));
        int size = list2.size();
        Assertions.assertEquals(list.size(), size);

        for (int i = 0; i < size; i++) {
            Assertions.assertEquals(list.get(i), list2.get(i));
        }
    }

}
