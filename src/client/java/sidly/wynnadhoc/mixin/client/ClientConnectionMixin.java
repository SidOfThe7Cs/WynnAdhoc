package sidly.wynnadhoc.mixin.client;

import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.PacketStuff;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(method = "sendInternal", at = @At("HEAD"))
    public void onPacket(Packet<?> packet, @Nullable ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
        PacketStuff.serverBoundPacket(packet, listener, flush);
    }

    @Inject(method = "handlePacket", at = @At("HEAD"))
    private static void onPacket(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        PacketStuff.clientBoundPacker(packet, false);
    }
}
