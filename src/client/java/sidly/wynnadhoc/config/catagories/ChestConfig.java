package sidly.wynnadhoc.config.catagories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class ChestConfig {
    @Expose
    @ConfigOption(name = "Auto Close", desc = "automatically takes favorited items and then closes loot chests")
    @ConfigEditorBoolean
    public boolean autoCloseChests = false;
}
