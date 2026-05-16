package sidly.wynnadhoc.mixin.client;

import com.wynntils.models.spells.QueuedMeleeScheduler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sidly.wynnadhoc.config.ConfigManager;

@Mixin(QueuedMeleeScheduler.class)
public class AutoAttackMixin {
    @Inject(method = "isMeleeReady", at = @At("HEAD"), cancellable = true)
    private static void isMeleeReady(CallbackInfoReturnable<Boolean> cir) {
        if (ConfigManager.INSTANCE.config.spell.toggleSpellcaster) cir.setReturnValue(false);
    }
}
