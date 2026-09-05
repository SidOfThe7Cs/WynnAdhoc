package sidly.wynnadhoc.features.c2c.packets;


import net.minecraft.network.NetworkSide;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;
import sidly.wynnadhoc.features.c2c.C2CFriendlyByteBuf;
import sidly.wynnadhoc.features.c2c.C2CPacket;
import sidly.wynnadhoc.features.c2c.C2CPacketListener;

public record ConnectionResponseC2CPacket(String sender, String publicKey) implements C2CPacket {
    public static final PacketCodec<C2CFriendlyByteBuf, ConnectionResponseC2CPacket> CODEC = Packet.createCodec(ConnectionResponseC2CPacket::write, ConnectionResponseC2CPacket::new);
    public static final PacketType<ConnectionResponseC2CPacket> ID = new PacketType<>(NetworkSide.CLIENTBOUND, Identifier.of("clientcommands", "connection_response"));

    public ConnectionResponseC2CPacket(C2CFriendlyByteBuf buf) {
        this(buf.getSender(), buf.readString());
    }

    public void write(C2CFriendlyByteBuf buf) {
        buf.writeString(publicKey);
    }

    @Override
    public PacketType<? extends Packet<C2CPacketListener>> getPacketType() {
        return ID;
    }

    @Override
    public void apply(C2CPacketListener listener) {
        listener.onConnectionResponseC2CPacket(this);
    }
}
