package sidly.wynnadhoc.mixin.client.loggingdisabler;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.wynntils.features.inventory.PersonalStorageUtilitiesFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.DebugConfig;

@Mixin(PersonalStorageUtilitiesFeature.class)
public class PersonalStorageMixin {
    @WrapOperation(
            method = "jumpToDestination",
            at = @At(value = "INVOKE", target = "Lcom/wynntils/core/WynntilsMod;info(Ljava/lang/String;)V"),
            remap = false
    )
    private static void jumping(String msg, Operation<Void> original) {
        if (!ConfigManager.INSTANCE.config.debug.disabledLogging.contains(DebugConfig.ChatLoggingTypes.PAGE_NAVIGATION)) {
            original.call(msg);
        }
    }

    @WrapOperation(
            method = "clickNextPage",
            at = @At(value = "INVOKE", target = "Lcom/wynntils/core/WynntilsMod;info(Ljava/lang/String;)V"),
            remap = false
    )
    private static void next(String msg, Operation<Void> original) {
        if (!ConfigManager.INSTANCE.config.debug.disabledLogging.contains(DebugConfig.ChatLoggingTypes.PAGE_NAVIGATION)) {
            original.call(msg);
        }
    }

    @WrapOperation(
            method = "clickPreviousPage",
            at = @At(value = "INVOKE", target = "Lcom/wynntils/core/WynntilsMod;info(Ljava/lang/String;)V"),
            remap = false
    )
    private static void prev(String msg, Operation<Void> original) {
        if (!ConfigManager.INSTANCE.config.debug.disabledLogging.contains(DebugConfig.ChatLoggingTypes.PAGE_NAVIGATION)) {
            original.call(msg);
        }
    }

    @WrapOperation(
            method = "tryToQuickJump",
            at = @At(value = "INVOKE", target = "Lcom/wynntils/core/WynntilsMod;info(Ljava/lang/String;)V"),
            remap = false
    )
    private static void quick(String msg, Operation<Void> original) {
        if (!ConfigManager.INSTANCE.config.debug.disabledLogging.contains(DebugConfig.ChatLoggingTypes.PAGE_NAVIGATION)) {
            original.call(msg);
        }
    }
}
