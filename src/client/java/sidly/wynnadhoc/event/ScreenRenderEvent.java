package sidly.wynnadhoc.event;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

// this renders only in a screen
public class ScreenRenderEvent extends Event<ScreenRenderEvent> {
    public Screen screen;
    public DrawContext context;
    public int mouseX;
    public int mouseY;

    public ScreenRenderEvent(Screen screen, DrawContext context, int mouseX, int mouseY) {
        this.screen = screen;
        this.context = context;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.fire();
    }
}
