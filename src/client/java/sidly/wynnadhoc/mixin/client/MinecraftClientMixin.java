package sidly.wynnadhoc.mixin.client;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.features.BowSpammer;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
    private void onDoItemUse(CallbackInfo ci) {
        if (BowSpammer.holdingItem() && ConfigManager.INSTANCE.config.toggles.bowSpammerToggle) {
            ci.cancel();
        }
    }
}
