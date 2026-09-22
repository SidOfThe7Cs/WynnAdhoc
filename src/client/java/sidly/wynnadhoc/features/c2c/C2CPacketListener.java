package sidly.wynnadhoc.features.c2c;

import net.minecraft.network.listener.ClientPacketListener;
import sidly.wynnadhoc.features.c2c.packets.*;

public interface C2CPacketListener extends ClientPacketListener {
    void onMessageC2CPacket(MessageC2CPacket packet);

    void onConnectionRequestC2CPacket(ConnectionRequestC2CPacket packet);

    void onConnectionResponseC2CPacket(ConnectionResponseC2CPacket connectionResponseC2CPacket);

    void onLarbonicJoinC2CPacket(LarbonicJoinRequestC2CPacket larbonicJoinRequestC2CPacket);

    void onLarbonicUpdateC2CPacket(LarbonicPartyMemberUpdateC2CPacket larbonicPartyMemberUpdateC2CPacket);
}
