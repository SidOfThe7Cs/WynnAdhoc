package sidly.wynnadhoc.config.catagories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class ChestConfig {
    @Expose
    @ConfigOption(name = "Auto Close", desc = "Automatically takes favorited items and then closes loot chests")
    @ConfigEditorBoolean
    public boolean autoCloseChests = false;

    @Expose
    @ConfigOption(name = "Force Chest Esp", desc = "Turns on chest esp")
    @ConfigEditorBoolean
    public boolean forceEsp = false;

    @Expose
    @ConfigOption(name = "Max Distance", desc = "Max distance to show chest esp")
    @ConfigEditorSlider(minValue = 0.0F, maxValue = 1000.0F, minStep = 5.0F)
    public double maxEspDistance = 150.0F;
}
