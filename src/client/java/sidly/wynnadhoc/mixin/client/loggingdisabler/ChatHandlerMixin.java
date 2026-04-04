package sidly.wynnadhoc.mixin.client.loggingdisabler;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.wynntils.handlers.chat.ChatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.DebugConfig;

@Mixin(value = ChatHandler.class, remap = false)
public class ChatHandlerMixin {
    @WrapOperation(
            method = "processChatMessage",
            at = @At(value = "INVOKE", target = "Lcom/wynntils/core/WynntilsMod;info(Ljava/lang/String;)V")
    )
    private void info(String msg, Operation<Void> original) {
        if (!ConfigManager.INSTANCE.config.debug.disabledLogging.contains(DebugConfig.ChatLoggingTypes.CHAT_TYPE_MESSAGES)) {
            original.call(msg);
        }
    }
}
