package su.windmill.bytes.socket.expansion.packet;

public interface Packet {

    PacketType<? extends Packet> type();

}
