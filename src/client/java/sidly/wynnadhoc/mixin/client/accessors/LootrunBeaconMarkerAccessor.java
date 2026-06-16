package sidly.wynnadhoc.mixin.client.accessors;

import com.wynntils.models.lootrun.markers.LootrunBeaconMarkerProvider;
import com.wynntils.models.marker.type.MarkerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = LootrunBeaconMarkerProvider.class, remap = false)
public interface LootrunBeaconMarkerAccessor {
    @Accessor(value = "taskMarkers", remap = false)
    List<MarkerInfo> getTaskMarkers();
}