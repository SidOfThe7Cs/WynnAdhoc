package sidly.wynnadhoc.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.event.GuiRenderEvent;
import sidly.wynnadhoc.event.GuiRenderOnTopEvent;
import sidly.wynnadhoc.event.ScreenRenderEvent;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    @Inject(method = "renderMain", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = At.Shift.AFTER))
    private void renderBackgroundTexture(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().player != null) {
            new GuiRenderEvent(context);
            GuiRenderOnTopEvent.onGuiRender(context);
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return;
        if ((Object) this instanceof HandledScreen<?> handledScreen) {
            new ScreenRenderEvent(handledScreen, context, mouseX, mouseY);
            new GuiRenderOnTopEvent(context);
        }
    }
}
