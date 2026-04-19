package sidly.wynnadhoc.config.catagories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

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
    @ConfigOption(name = "Custom Prof Nodes", desc = "Replace prof nodes")
    @ConfigEditorBoolean
    public boolean customProfNodes = true;
}
