package sidly.wynnadhoc.mixin.client.accessors;


import com.wynntils.models.lootrun.LootrunBeaconModel;
import com.wynntils.models.lootrun.markers.LootrunBeaconMarkerProvider;
import com.wynntils.models.lootrun.type.LootrunLocation;
import com.wynntils.models.lootrun.type.TaskLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Set;

@Mixin(value = LootrunBeaconModel.class, remap = false)
public interface WynntillsLootrunModelAccessor {
    @Accessor(value = "possibleTaskLocations", remap = false)
    Set<TaskLocation> getPossibleTaskLocations();

    @Accessor(value = "taskLocations", remap = false)
    Map<LootrunLocation, Set<TaskLocation>> getTaskLocations();

    @Accessor(value = "LOOTRUN_BEACON_COMPASS_PROVIDER", remap = false)
    LootrunBeaconMarkerProvider getLOOTRUN_BEACON_COMPASS_PROVIDER();
}
