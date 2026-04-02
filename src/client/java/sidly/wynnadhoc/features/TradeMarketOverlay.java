package sidly.wynnadhoc.features;

import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.SimpleFeatureToggles;
import sidly.wynnadhoc.config.gui.GuiElement;
import sidly.wynnadhoc.config.gui.HudElementManager;
import sidly.wynnadhoc.config.gui.TextHudComponent;

public class TradeMarketOverlay {
    private static SimpleFeatureToggles config() {
        return ConfigManager.INSTANCE.config.toggles;
    }

    private static final GuiElement overlay = new GuiElement(
            config().TMOverlayMain,
            new TextHudComponent(
                    config().TMOverlayTitle,
                    TradeMarketCore.INSTANCE::showOverlay,
                    TradeMarketCore.INSTANCE::updateDisplay
            )
    );

    public static void registerHudElements() {
        HudElementManager.register(overlay);
    }
}
