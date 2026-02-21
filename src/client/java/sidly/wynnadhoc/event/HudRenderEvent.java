package sidly.wynnadhoc.event;

import net.minecraft.client.gui.DrawContext;

// renders on the ingame hud and not in screens
public class HudRenderEvent extends Event<HudRenderEvent> {
    public DrawContext context;

    public HudRenderEvent(DrawContext context) {
        this.context = context;
        this.fire();
    }
}
