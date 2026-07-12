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
    @ConfigOption(name = "Disable Darkness", desc = "Disables all darkness effects")
    @ConfigEditorBoolean
    public boolean disableDarkness = true;

    @Expose
    @ConfigOption(name = "Hide Ing Pouch Tooltip", desc = "Hides the tooltip of the ingredient pouch")
    @ConfigEditorBoolean
    public boolean hideIngredientPouchTooltip = false;

    @Expose
    @ConfigOption(name = "Custom Prof Nodes", desc = "Replace prof nodes")
    @ConfigEditorBoolean
    public boolean customProfNodes = false;

    @Expose
    @ConfigOption(name = "Wind Prison Box", desc = "Draw a box around mobs with wind prison as well as the time remaining")
    @ConfigEditorBoolean
    public boolean showWindPrisonBox = false;

    @Expose
    @ConfigOption(name = "Marks Count", desc = "Draw marks count on mobs")
    @ConfigEditorBoolean
    public boolean showMarksCount = false;
}
