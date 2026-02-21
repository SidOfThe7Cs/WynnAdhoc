package sidly.wynnadhoc.config.catagories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class SimpleFeatureToggles {
    @Expose
    @ConfigOption(name = "Bow Spammer", desc = "sends a right click packet every 5 ticks when holding right click with a bow")
    @ConfigEditorBoolean
    public boolean bowSpammerToggle = true;

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
    @ConfigOption(name = "Disable Darkness", desc = "Disables all darkness effects")
    @ConfigEditorBoolean
    public boolean disableDarkness = true;
}
