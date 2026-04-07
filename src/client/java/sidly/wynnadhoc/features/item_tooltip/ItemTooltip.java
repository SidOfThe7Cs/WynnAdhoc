package sidly.wynnadhoc.features.item_tooltip;


import net.minecraft.text.Text;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.event.DrawTooltipEvent;
import sidly.wynnadhoc.utils.Debug;
import sidly.wynnadhoc.utils.resource_pack.FontUtils;

public class ItemTooltip {
    public static void onTooltipDraw(DrawTooltipEvent event) {
        for (Text line : event.tooltip) {
            WynnAdhocClient.LOGGER.info(Debug.Type.TEMP, FontUtils.translate(line));
        }
    }
}
