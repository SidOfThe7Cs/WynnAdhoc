package sidly.wynnadhoc.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.wynntils.services.hades.HadesService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.SimpleFeatureToggles;

@Mixin(HadesService.class)
public class HadesMixin {
    @ModifyExpressionValue(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasStatusEffect(Lnet/minecraft/registry/entry/RegistryEntry;)Z"
            ),
            method = "onTick"
    )
    public boolean hasStatusEffectOverride(boolean original) {
        SimpleFeatureToggles.NightVisionOption forceNightVision = ConfigManager.INSTANCE.config.toggles.forceNightVision;
        if (forceNightVision == SimpleFeatureToggles.NightVisionOption.ALWAYS || forceNightVision == SimpleFeatureToggles.NightVisionOption.ONLY_END)
            return true;
        else return original;
    }
}
