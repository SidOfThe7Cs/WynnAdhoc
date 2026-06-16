package sidly.wynnadhoc.mixin.client.accessors;


import com.wynntils.models.lootrun.LootrunModel;
import com.wynntils.models.lootrun.type.TaskLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(value = LootrunModel.class, remap = false)
public interface WynntillsLootrunModelAccessor {
    @Accessor(value = "possibleTaskLocations", remap = false)
    static Set<TaskLocation> getPossibleTaskLocations() {
        return null;
    }
}
