package sidly.wynnadhoc.features.item_tooltip;


import net.minecraft.text.Text;
import sidly.wynnadhoc.event.DrawTooltipEvent;

public class ItemTooltip {
    public static void onTooltipDraw(DrawTooltipEvent event) {
        for (Text line : event.tooltip) {
        }
    }
}
