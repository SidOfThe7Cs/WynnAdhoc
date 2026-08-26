package sidly.wynnadhoc.features.item_tooltip

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.event.DrawTooltipEvent
import sidly.wynnadhoc.mixin.client.accessors.HandledScreenAccessor
import sidly.wynnadhoc.mixin.client.accessors.SlotAccessor
import sidly.wynnadhoc.utils.text.ItemTextParser

object ItemTooltip {
    fun onTooltipDraw(event: DrawTooltipEvent) {
        hideIngPouch(event)
    }

    fun hideIngPouch(event: DrawTooltipEvent) {
        if (!ConfigManager.INSTANCE.config.toggles.hideIngredientPouchTooltip) return
        val currentScreen = MinecraftClient.getInstance().currentScreen
        if (currentScreen is HandledScreen<*>) {
            val slot = ((currentScreen as? HandledScreenAccessor)?.focusedSlot as? SlotAccessor)?.index ?: return
            if (slot != 13) return

            val textParser = ItemTextParser(event.tooltip)
            if (textParser.isIngredientPouch()) event.hide()
        }
    }
}
