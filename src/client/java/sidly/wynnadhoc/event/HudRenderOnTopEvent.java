package sidly.wynnadhoc.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

// this renders on both the hud and in screens
public class HudRenderOnTopEvent extends Event<HudRenderOnTopEvent> {
    public static void onGuiRender(DrawContext context) {
        if (MinecraftClient.getInstance().currentScreen != null) return;
        new HudRenderOnTopEvent(context);
    }

    public DrawContext context;

    public HudRenderOnTopEvent(DrawContext context) {
        this.context = context;
        this.fire();
    }
}
