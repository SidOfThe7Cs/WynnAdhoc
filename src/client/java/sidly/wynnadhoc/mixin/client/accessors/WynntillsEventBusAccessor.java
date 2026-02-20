package sidly.wynnadhoc.mixin.client.accessors;

import com.wynntils.core.WynntilsMod;
import net.neoforged.bus.api.IEventBus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = WynntilsMod.class, remap = false)
public interface WynntillsEventBusAccessor {
    @Accessor(value = "eventBus", remap = false)
    static IEventBus getEventBus() {
        return null;
    }
}
