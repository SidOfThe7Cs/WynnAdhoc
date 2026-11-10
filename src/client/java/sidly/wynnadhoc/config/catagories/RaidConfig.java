package sidly.wynnadhoc.config.catagories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class RaidConfig {
    @Expose
    @ConfigOption(name = "Show Maze Path", desc = "Renders the barriers in the maze path")
    @ConfigEditorBoolean
    public boolean showMazePath = true;
}
