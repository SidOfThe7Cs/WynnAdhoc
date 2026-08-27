package sidly.wynnadhoc.mixin.client;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.models.CameraModel;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "updateCamera", at = @At("HEAD"))
    public void onCameraUpdate(Camera camera, Frustum frustum, boolean spectator, CallbackInfo ci) {
        CameraModel.INSTANCE.setLastFrustum(frustum);
    }
}
