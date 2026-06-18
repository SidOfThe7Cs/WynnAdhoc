package sidly.wynnadhoc.event;

import net.minecraft.text.Text;

import java.util.List;

public class DrawTooltipEvent extends Event<DrawTooltipEvent> {

    public List<Text> tooltip;

    public DrawTooltipEvent(List<Text> originalReturn) {
        this.tooltip = originalReturn;
        this.fire();
    }
}
