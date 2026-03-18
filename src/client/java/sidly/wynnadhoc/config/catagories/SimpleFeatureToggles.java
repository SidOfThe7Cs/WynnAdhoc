package sidly.wynnadhoc.config.catagories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class SimpleFeatureToggles {
    @Expose
    @ConfigOption(name = "Bow Spammer", desc = "sends a right click packet when holding right click with a bow every amount of ticks 0 is off 1 is every tick. wynntills auto attack already does this")
    @ConfigEditorSlider(minValue = 0, maxValue = 6, minStep = 1)
    public int bowSpammerToggle = 0;

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
