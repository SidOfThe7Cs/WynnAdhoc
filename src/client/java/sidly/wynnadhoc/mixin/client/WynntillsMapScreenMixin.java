package sidly.wynnadhoc.mixin.client;

import com.wynntils.screens.maps.AbstractMapScreen;
import com.wynntils.screens.maps.MainMapScreen;
import com.wynntils.screens.maps.widgets.MapButton;
import com.wynntils.services.map.pois.Poi;
import com.wynntils.utils.render.Texture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.features.chests.CustomWynntillsMapWaypoints;

import java.util.List;

@Mixin(value = MainMapScreen.class, remap = false)
public class WynntillsMapScreenMixin extends AbstractMapScreen {
    @Inject(method = "doInit", at = @At("RETURN"))
    public void onInit(CallbackInfo ci) {
        super.addMapButton(new MapButton(
                Texture.CHEST_T1,
                (b) -> MinecraftClient.getInstance().setScreen(new CustomWynntillsMapWaypoints.SelectorScreen((MainMapScreen) (Object) this)),
                List.of(
                        Text.literal("[WynnAdhoc] Click to filter chest waypoints")
                )
        ));
    }

    @ModifyArg(
            method = "renderPois(Lnet/minecraft/client/gui/DrawContext;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/wynntils/screens/maps/MainMapScreen;renderPois(Ljava/util/List;Lnet/minecraft/client/gui/DrawContext;Lcom/wynntils/utils/type/BoundingBox;FII)V"
            ),
            index = 0
    )
    private List<Poi> renderPois(List<Poi> pois) {
        pois.addAll(CustomWynntillsMapWaypoints.getSelectedPois());
        return pois;
    }
}
