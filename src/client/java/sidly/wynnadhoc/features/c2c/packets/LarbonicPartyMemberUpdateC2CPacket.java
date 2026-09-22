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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public record LarbonicPartyMemberUpdateC2CPacket(@Nullable String sender,
                                                 Set<String> currentMembers) implements C2CPacket {
    public static final PacketCodec<C2CFriendlyByteBuf, LarbonicPartyMemberUpdateC2CPacket> CODEC = Packet.createCodec(LarbonicPartyMemberUpdateC2CPacket::write, LarbonicPartyMemberUpdateC2CPacket::new);
    public static final PacketType<LarbonicPartyMemberUpdateC2CPacket> ID = new PacketType<>(NetworkSide.CLIENTBOUND, Identifier.of("clientcommands", "larbonic_update"));

    public LarbonicPartyMemberUpdateC2CPacket(C2CFriendlyByteBuf buf) {
        this(buf.getSender(), Arrays.stream(buf.readString().split("\n")).collect(Collectors.toSet()));
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
        String concatMembers = String.join("\n", currentMembers);
        buf.writeString(concatMembers);
    }

    @Override
    public void apply(C2CPacketListener listener) {
        listener.onLarbonicUpdateC2CPacket(this);
    }
}
