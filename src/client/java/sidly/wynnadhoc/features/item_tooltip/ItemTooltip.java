package sidly.wynnadhoc.features.item_tooltip;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.event.DrawTooltipEvent;
import sidly.wynnadhoc.mixin.client.accessors.HandledScreenAccessor;
import sidly.wynnadhoc.mixin.client.accessors.SlotAccessor;

public class ItemTooltip {
    public static void onTooltipDraw(DrawTooltipEvent event) {
        hideIngPouch(event);
    }

    public static void hideIngPouch(DrawTooltipEvent event) {
        if (!ConfigManager.INSTANCE.config.toggles.hideIngredientPouchTooltip) return;
        Screen currentScreen = MinecraftClient.getInstance().currentScreen;
        if (currentScreen instanceof HandledScreen<?> handlesScreen) {
            String title = event.tooltip.getFirst().getString();
            HandledScreenAccessor screenAcc = (HandledScreenAccessor) handlesScreen;
            SlotAccessor slot = (SlotAccessor) screenAcc.getFocusedSlot();
            if (title.equals("§6Ingredient Pouch") && slot.getIndex() == 13) {
                event.hide();
            }
        }
    }
}
