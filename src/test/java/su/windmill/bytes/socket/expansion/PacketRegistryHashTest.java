package su.windmill.bytes.socket.expansion;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import su.windmill.bytes.codec.Codec;
import su.windmill.bytes.socket.expansion.packet.Packet;
import su.windmill.bytes.socket.expansion.packet.PacketRegistry;
import su.windmill.bytes.socket.expansion.packet.PacketType;
import su.windmill.bytes.socket.expansion.packet.PacketWithoutResponse;
import su.windmill.bytes.util.Key;

import java.util.function.Consumer;

public class PacketRegistryHashTest {

    private static final PacketType<TestPacket> TEST_TYPE = new PacketType<>(
            PacketType.Flow.SERVERBOUND,
            Key.key("test"),
            Codec.fixed(
                    (_, _) -> {},
                    (_) -> new TestPacket()
            )
    );

    @Test
    public void test() {
        PacketRegistry first = new TestRegistry();
        PacketRegistry second = new TestRegistry();
        PacketRegistry third = new PacketRegistry();

        Assertions.assertEquals(first.hash(), second.hash());
        Assertions.assertNotEquals(first.hash(), third.hash());
    }

    private static class TestRegistry extends PacketRegistry {
        @Override
        public void initialize(Consumer<PacketType<? extends Packet>> registrar) {
            registrar.accept(TEST_TYPE);
        }
    }

    private static class TestPacket implements PacketWithoutResponse {

        @Override
        public PacketType<? extends Packet> type() {
            return TEST_TYPE;
        }

    }

}
