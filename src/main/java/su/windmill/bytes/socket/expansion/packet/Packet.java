package su.windmill.bytes.socket.expansion.packet;

/**
 * Packet shared interface for separating PacketWithoutResponse and PacketWithResponse
 */
public interface Packet {

    /**
     * @return packet type
     */
    PacketType<? extends Packet> type();

}
