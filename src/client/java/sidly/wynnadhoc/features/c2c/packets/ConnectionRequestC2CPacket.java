package sidly.wynnadhoc.features.c2c.packets;


import net.minecraft.network.NetworkSide;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;
import sidly.wynnadhoc.features.c2c.C2CFriendlyByteBuf;
import sidly.wynnadhoc.features.c2c.C2CPacket;
import sidly.wynnadhoc.features.c2c.C2CPacketListener;

public record ConnectionRequestC2CPacket(String sender, String publicKey) implements C2CPacket {
    public static final PacketCodec<C2CFriendlyByteBuf, ConnectionRequestC2CPacket> CODEC = Packet.createCodec(ConnectionRequestC2CPacket::write, ConnectionRequestC2CPacket::new);
    public static final PacketType<ConnectionRequestC2CPacket> ID = new PacketType<>(NetworkSide.CLIENTBOUND, Identifier.of("clientcommands", "connection_request"));

    public ConnectionRequestC2CPacket(C2CFriendlyByteBuf buf) {
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
        listener.onConnectionRequestC2CPacket(this);
    }
}
