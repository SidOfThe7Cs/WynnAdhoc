package sidly.wynnadhoc.event;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public class ScreenRenderEvent extends Event<ScreenRenderEvent> {
    public HandledScreen<?> screen;
    public DrawContext context;
    public int mouseX;
    public int mouseY;

    public ScreenRenderEvent(HandledScreen<?> screen, DrawContext context, int mouseX, int mouseY) {
        this.screen = screen;
        this.context = context;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.fire();
    }
}
