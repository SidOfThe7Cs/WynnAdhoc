package sidly.wynnadhoc.mixin.client;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.SimpleFeatureToggles;
import sidly.wynnadhoc.utils.LocationUtils;

@Mixin(LivingEntity.class)
public class StatusEffectMixin {
    @Unique
    private static SimpleFeatureToggles config() {
        return ConfigManager.INSTANCE.config.toggles;
    }

    @Inject(method = "hasStatusEffect", at = @At("HEAD"), cancellable = true)
    private void hasStatusEffect(RegistryEntry<StatusEffect> effect, CallbackInfoReturnable<Boolean> cir) {
        if (effect == StatusEffects.NIGHT_VISION) {
            switch (config().forceNightVision) {
                case OFF -> {}
                case ONLY_END -> {
                    if (LocationUtils.isInEndBiome()) cir.setReturnValue(true);
                }
                case ALWAYS -> cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "getStatusEffect", at = @At("HEAD"), cancellable = true)
    private void getStatusEffect(RegistryEntry<StatusEffect> effect, CallbackInfoReturnable<StatusEffectInstance> cir) {
        if (effect == StatusEffects.NIGHT_VISION) {
            StatusEffectInstance nightVision = new StatusEffectInstance(StatusEffects.NIGHT_VISION, -1, 0, true, false);
            switch (config().forceNightVision) {
                case OFF -> {}
                case ONLY_END -> {
                    if (LocationUtils.isInEndBiome()) cir.setReturnValue(nightVision);
                }
                case ALWAYS -> cir.setReturnValue(nightVision);
            }
        }
    }
}

