package sidly.wynnadhoc.config.catagories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import sidly.wynnadhoc.config.gui.HudElementData;

public class GuildConfig {
    @Expose
    @ConfigOption(name = "Show Aspect Overlay", desc = "Raid completions in guild log")
    @ConfigEditorBoolean
    public boolean showAspectsOverlay = true;

    @Expose
    public HudElementData aspectOverlayData = new HudElementData(
            "Aspect Overlay",
            20,
            20,
            1.0f
    );
}
