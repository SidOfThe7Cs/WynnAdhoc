package sidly.wynnadhoc.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.event.HudRenderOnTopEvent;
import sidly.wynnadhoc.event.ScreenRenderEvent;

@Mixin(RecipeBookScreen.class)
public class RecipeBookScreenMixin {
    // recipe books creen does not call super.render inits render function
    @Inject(method = "render", at = @At("TAIL"))
    public void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        new ScreenRenderEvent((Screen) (Object) this, context, mouseX, mouseY);
        new HudRenderOnTopEvent(context);
    }
}
