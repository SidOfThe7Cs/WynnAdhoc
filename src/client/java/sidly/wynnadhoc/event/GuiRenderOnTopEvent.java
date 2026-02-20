package sidly.wynnadhoc.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class GuiRenderOnTopEvent extends Event<GuiRenderOnTopEvent> {
    public static void onGuiRender(DrawContext context) {
        if (MinecraftClient.getInstance().currentScreen != null) return;
        new GuiRenderOnTopEvent(context);
    }

    public DrawContext context;
    public GuiRenderOnTopEvent(DrawContext context) {
        this.context = context;
        this.fire();
    }
}
