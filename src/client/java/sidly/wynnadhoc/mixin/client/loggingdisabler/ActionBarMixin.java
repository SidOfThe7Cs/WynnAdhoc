package sidly.wynnadhoc.mixin.client.loggingdisabler;

import com.wynntils.handlers.actionbar.ActionBarHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.DebugConfig;

@Mixin(value = ActionBarHandler.class, remap = false)
public class ActionBarMixin {
    @Inject(method = "debugChecks", at = @At("HEAD"), remap = false, cancellable = true)
    private static void disableDebugChecks(CallbackInfo ci) {
        if (ConfigManager.INSTANCE.config.debug.disabledLogging.contains(DebugConfig.ChatLoggingTypes.ACTION_BAR_FAILURE)) {
            ci.cancel();
        }
    }
}
