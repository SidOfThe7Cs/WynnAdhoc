package sidly.wynnadhoc.features.item_tooltip;


import net.minecraft.text.Text;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.event.DrawTooltipEvent;
import sidly.wynnadhoc.utils.Debug;

public class ItemTooltip {
    public static void onTooltipDraw(DrawTooltipEvent event) {
        for (Text line : event.tooltip) {
            WynnAdhocClient.LOGGER.info(Debug.Type.TEMP, line.getString());
        }
    }
}
