package sidly.wynnadhoc.mixin.client.loggingdisabler;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.DebugConfig;

@Mixin(targets = "com.wynntils.core.events.EventBusWrapper$DevelopmentEnvironment", remap = false)
public class PongEventMixin {
    @WrapOperation(
            method = "post",
            at = @At(value = "INVOKE", target = "Lcom/wynntils/core/WynntilsMod;warn(Ljava/lang/String;)V", ordinal = 1),
            remap = false
    )
    private void info(String msg, Operation<Void> original) {
        if (!ConfigManager.INSTANCE.config.debug.disabledLogging.contains(DebugConfig.ChatLoggingTypes.DEV_PONG_EVENT)) {
            original.call(msg);
        }
    }
}
