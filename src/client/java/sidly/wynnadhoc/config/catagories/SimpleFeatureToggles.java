package sidly.wynnadhoc.config.catagories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import sidly.wynnadhoc.config.gui.HudComponentData;
import sidly.wynnadhoc.config.gui.SubViewPort;

public class SimpleFeatureToggles {
    @Expose
    @ConfigOption(name = "Force Night Vision", desc = "When should the night vision effect be applied to the client")
    @ConfigEditorDropdown
    public NightVisionOption forceNightVision = NightVisionOption.OFF;

    public enum NightVisionOption {
        OFF,
        ONLY_END,
        ALWAYS
    }

    @Expose
    @ConfigOption(name = "Show Trade Market Filter", desc = "show a gui to allow tm highlighting based on specific identification rolls")
    @ConfigEditorBoolean
    public boolean showTradeMarketFilter = true;

    @Expose
    public HudComponentData TMOverlayTitle = new HudComponentData("TMTitle", 0.5f, 0.1f, 1f);
    @Expose
    public SubViewPort TMOverlayMain = new SubViewPort(0.2f, 0.2f, 20, 50, 0xCC1a1a1a);
}
