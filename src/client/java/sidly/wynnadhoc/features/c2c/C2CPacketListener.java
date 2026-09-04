package sidly.wynnadhoc.features.c2c;

import net.minecraft.network.listener.ClientPacketListener;
import sidly.wynnadhoc.features.c2c.packets.MessageC2CPacket;

public interface C2CPacketListener extends ClientPacketListener {
    void onMessageC2CPacket(MessageC2CPacket packet);
}
