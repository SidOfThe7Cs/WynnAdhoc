package sidly.wynnadhoc.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.event.TextDisplaySyncEvent;
import sidly.wynnadhoc.mixin.client.accessors.TextDisplayEntityAccessor;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onEntityTrackerUpdate", at = @At("TAIL"))
    private void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            if (client.world == null) return;
            Entity entity = client.world.getEntityById(packet.id());
            if (entity instanceof DisplayEntity.TextDisplayEntity textDisplay) {
                // Only process if text actually changed
                packet.trackedValues().forEach(entry -> {
                    if (entry.id() == TextDisplayEntityAccessor.getTextKey().id()) {
                        new TextDisplaySyncEvent(textDisplay);
                    }
                });
            }
        });
    }
}
