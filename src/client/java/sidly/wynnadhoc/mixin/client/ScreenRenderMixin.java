package sidly.wynnadhoc.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.event.ScreenRenderEvent;

@Mixin(Screen.class)
public abstract class ScreenRenderMixin {

    @Inject(method = "render", at = @At("TAIL"))
    public void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        if ((Object) this instanceof HandledScreen<?> handledScreen) {
            new ScreenRenderEvent(handledScreen, context, mouseX, mouseY);
        }

    }
}

