package sidly.wynnadhoc.event;

import net.minecraft.text.Text;

import java.util.List;

public class DrawTooltipEvent extends Event<DrawTooltipEvent> {
    public List<Text> tooltip;
    public int x, y;
    public boolean hidden = false;

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void hide() {
        this.hidden = true;
    }

    public DrawTooltipEvent(List<Text> originalReturn, int x, int y) {
        this.tooltip = originalReturn;
        this.x = x;
        this.y = y;
        this.fire();
    }
}
