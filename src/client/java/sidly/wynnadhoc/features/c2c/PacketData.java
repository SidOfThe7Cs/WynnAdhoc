package sidly.wynnadhoc.features.c2c;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.packet.Packet;

public record PacketData(Packet<C2CPacketListener> packet, String recipient) {
    public void send() {
        try {
            C2CPacketHandler.getInstance().sendPacket(packet, recipient);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
