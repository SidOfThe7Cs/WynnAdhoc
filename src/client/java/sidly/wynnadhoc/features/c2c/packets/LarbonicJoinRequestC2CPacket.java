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

public record LarbonicJoinRequestC2CPacket(@Nullable String sender) implements C2CPacket {
    public static final PacketCodec<C2CFriendlyByteBuf, LarbonicJoinRequestC2CPacket> CODEC = Packet.createCodec(LarbonicJoinRequestC2CPacket::write, LarbonicJoinRequestC2CPacket::new);
    public static final PacketType<LarbonicJoinRequestC2CPacket> ID = new PacketType<>(NetworkSide.CLIENTBOUND, Identifier.of("clientcommands", "larbonic_join"));

    public LarbonicJoinRequestC2CPacket(C2CFriendlyByteBuf buf) {
        this(buf.getSender());
    }

    @Override
    public @Nullable String sender() {
        return sender;
    }

    @Override
    public PacketType<? extends Packet<C2CPacketListener>> getPacketType() {
        return ID;
    }

    public void write(C2CFriendlyByteBuf buf) {
    }

    @Override
    public void apply(C2CPacketListener listener) {
        listener.onLarbonicJoinC2CPacket(this);
    }
}
