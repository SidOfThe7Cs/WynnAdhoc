package sidly.wynnadhoc.features.c2c.packets;

import net.minecraft.network.NetworkSide;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;
import sidly.wynnadhoc.features.c2c.C2CFriendlyByteBuf;
import sidly.wynnadhoc.features.c2c.C2CPacket;
import sidly.wynnadhoc.features.c2c.C2CPacketListener;

public record MessageC2CPacket(@Nullable String sender, String message) implements C2CPacket {
    public static final PacketCodec<C2CFriendlyByteBuf, MessageC2CPacket> CODEC = Packet.createCodec(MessageC2CPacket::write, MessageC2CPacket::new);
    public static final PacketType<MessageC2CPacket> ID = new PacketType<>(NetworkSide.CLIENTBOUND, Identifier.of("clientcommands", "message"));

    public MessageC2CPacket(C2CFriendlyByteBuf buf) {
        this(buf.getSender(), buf.readString());
    }

    public void write(C2CFriendlyByteBuf buf) {
        buf.writeString(this.message);
    }

    @Override
    public PacketType<? extends Packet<C2CPacketListener>> getPacketType() {
        return ID;
    }

    @Override
    public void apply(C2CPacketListener listener) {
        listener.onMessageC2CPacket(this);
    }
}
