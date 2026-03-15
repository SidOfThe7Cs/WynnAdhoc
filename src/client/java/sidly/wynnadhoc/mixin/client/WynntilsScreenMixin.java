package sidly.wynnadhoc.mixin.client;

import com.wynntils.core.consumers.screens.WynntilsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.event.HudRenderOnTopEvent;
import sidly.wynnadhoc.event.ScreenRenderEvent;
import sidly.wynnadhoc.features.war.DB;

@Mixin(WynntilsScreen.class)
public class WynntilsScreenMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        new ScreenRenderEvent(client.currentScreen, context, mouseX, mouseY);
        new HudRenderOnTopEvent(context);
        DB.parseTerritoryScreen((Screen) (Object) this, false);
    }
}
