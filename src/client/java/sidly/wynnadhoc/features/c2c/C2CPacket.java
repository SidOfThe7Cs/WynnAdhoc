package sidly.wynnadhoc.features.c2c;

import net.minecraft.network.packet.Packet;
import org.jspecify.annotations.Nullable;

public interface C2CPacket extends Packet<C2CPacketListener> {
    @Nullable
    String sender();
}
