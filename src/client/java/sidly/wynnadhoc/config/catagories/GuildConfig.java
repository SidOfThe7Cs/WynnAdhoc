package sidly.wynnadhoc.config.catagories;

import com.google.gson.annotations.Expose;
import sidly.wynnadhoc.config.gui.HudElementData;

public class GuildConfig {
    /*
    TODO broken
    @Expose
    @ConfigOption(name = "Show Aspect Overlay", desc = "Raid completions in guild log")
    @ConfigEditorBoolean

     */
    public boolean showAspectsOverlay = false;

    @Expose
    public HudElementData aspectOverlayData = new HudElementData(
            "Aspect Overlay",
            0.65f,
            0.2f,
            1.0f
    );
}
