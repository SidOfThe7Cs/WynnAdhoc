package sidly.wynnadhoc.features

import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.config.gui.HudElementManager
import sidly.wynnadhoc.config.gui.GuiElement
import sidly.wynnadhoc.config.gui.TextHudComponent
import sidly.wynnadhoc.event.PreInitEvent

object TradeMarketOverlay {
    private val config get() = ConfigManager.INSTANCE.config.toggles

    private var overlay: GuiElement = GuiElement(
        config.TMOverlayMain,
        TextHudComponent(
            config.TMOverlayTitle,
            TradeMarketOverlay::showOverlay,
            TradeMarketOverlay::updateDisplay
        )
    )

    fun registerHudElements(empty: PreInitEvent) {
        HudElementManager.register(overlay)
    }

    fun showOverlay(): Boolean {
        return true
    }

    fun updateDisplay(): String {
        return "Trade Market Highlighters"
    }
}