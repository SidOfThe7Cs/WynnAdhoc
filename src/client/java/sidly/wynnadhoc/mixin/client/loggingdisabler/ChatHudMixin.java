package sidly.wynnadhoc.mixin.client.loggingdisabler;

import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.DebugConfig;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @Inject(method = "logChatMessage", at = @At("HEAD"), cancellable = true)
    private void info(ChatHudLine message, CallbackInfo ci) {
        if (ConfigManager.INSTANCE.config.debug.disabledLogging.contains(DebugConfig.ChatLoggingTypes.CHAT_MESSAGES)) {
            ci.cancel();
        }
    }
}
