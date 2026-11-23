package sidly.wynnadhoc.mixin.client;

import com.wynntils.models.lootrun.markers.LootrunBeaconMarkerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.features.lootruns.LootrunCore;

@Mixin(LootrunBeaconMarkerProvider.class)
public class LootrunBeaconMarkerMixin {
    @Inject(method = "reloadTaskMarkers", at = @At("RETURN"))
    public void onReload(CallbackInfo ci) {
        LootrunCore.INSTANCE.clearObscuredData();
    }
}
