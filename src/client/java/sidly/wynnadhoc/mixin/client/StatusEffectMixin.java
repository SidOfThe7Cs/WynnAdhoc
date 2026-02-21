package sidly.wynnadhoc.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.SimpleFeatureToggles;
import sidly.wynnadhoc.utils.DebugWindow;

import java.util.Optional;

@Mixin(LivingEntity.class)
public class StatusEffectMixin {
    @Unique private static SimpleFeatureToggles config() { return ConfigManager.INSTANCE.config.toggles; }
    @Inject(method = "hasStatusEffect", at = @At("HEAD"), cancellable = true)
    private void hasStatusEffect(RegistryEntry<StatusEffect> effect, CallbackInfoReturnable<Boolean> cir) {
        if (effect == StatusEffects.NIGHT_VISION) {
            switch (config().forceNightVision) {
                case OFF -> {}
                case ONLY_END -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.world != null && client.player != null) {
                        BlockPos playerPos = client.player.getBlockPos();
                        RegistryEntry<Biome> biomeEntry = client.world.getBiome(playerPos);

                        // Safely get the biome registry
                        Optional<Registry<Biome>> optionalRegistry = client.world.getRegistryManager().getOptional(RegistryKeys.BIOME);

                        if (optionalRegistry.isPresent()) {
                            Registry<Biome> biomeRegistry = optionalRegistry.get();
                            Identifier biomeId = biomeRegistry.getId(biomeEntry.value());

                            if (biomeId != null) {
                                if (biomeId.equals(Identifier.of("minecraft", "the_end"))) {
                                    cir.setReturnValue(true);
                                }
                            } else {
                                DebugWindow.getInstance().log(DebugWindow.Priority.ERROR,"Biome ID not found.");
                            }
                        } else {
                            DebugWindow.getInstance().log(DebugWindow.Priority.ERROR, "Biome registry not available.");
                        }
                    }
                }
                case ALWAYS -> cir.setReturnValue(true);
            }
        }
        if (effect == StatusEffects.BLINDNESS && config().disableDarkness) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getStatusEffect", at = @At("HEAD"), cancellable = true)
    private void getStatusEffect(RegistryEntry<StatusEffect> effect, CallbackInfoReturnable<StatusEffectInstance> cir) {
        if (effect == StatusEffects.NIGHT_VISION) {
            switch (config().forceNightVision) {
                case OFF -> {}
                case ONLY_END -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.world != null && client.player != null) {
                        BlockPos playerPos = client.player.getBlockPos();
                        RegistryEntry<Biome> biomeEntry = client.world.getBiome(playerPos);

                        // Safely get the biome registry
                        Optional<Registry<Biome>> optionalRegistry = client.world.getRegistryManager().getOptional(RegistryKeys.BIOME);

                        if (optionalRegistry.isPresent()) {
                            Registry<Biome> biomeRegistry = optionalRegistry.get();
                            Identifier biomeId = biomeRegistry.getId(biomeEntry.value());

                            if (biomeId != null) {
                                if (biomeId.equals(Identifier.of("minecraft", "the_end"))) {
                                    cir.setReturnValue(new StatusEffectInstance(StatusEffects.NIGHT_VISION, -1, 0, true, false));
                                }
                            } else {
                                DebugWindow.getInstance().log(DebugWindow.Priority.ERROR,"Biome ID not found.");
                            }
                        } else {
                            DebugWindow.getInstance().log(DebugWindow.Priority.ERROR,"Biome registry not available.");
                        }
                    }
                }
                case ALWAYS -> cir.setReturnValue(new StatusEffectInstance(StatusEffects.NIGHT_VISION, -1, 0, true, false));
            }
        }
        if (effect == StatusEffects.BLINDNESS && config().disableDarkness) {
            cir.setReturnValue(null);
        }
    }
}

