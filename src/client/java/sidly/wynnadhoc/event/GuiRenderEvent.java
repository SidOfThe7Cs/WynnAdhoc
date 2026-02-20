package sidly.wynnadhoc.event;

import net.minecraft.client.gui.DrawContext;

public class GuiRenderEvent extends Event<GuiRenderEvent> {
    DrawContext context;

    public GuiRenderEvent(DrawContext context) {
        this.context = context;
        this.fire();
    }
}
