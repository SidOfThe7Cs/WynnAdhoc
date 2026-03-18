package sidly.wynnadhoc.mixin.client;

import com.wynntils.core.WynntilsMod;
import net.neoforged.bus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sidly.wynnadhoc.event.NeoEvent;

@Mixin(value = WynntilsMod.class, remap = false)
public class WynntillsModMixin {
    @Inject(method = "postEvent", at = @At("HEAD"), remap = false)
    private static <T extends Event> void onPostEvent(T event, CallbackInfoReturnable<Boolean> cir) {
        NeoEvent.post(event);
    }
}
