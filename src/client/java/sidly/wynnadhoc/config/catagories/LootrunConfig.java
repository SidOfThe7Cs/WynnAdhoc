package sidly.wynnadhoc.config.catagories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import sidly.wynnadhoc.config.gui.HudElementData;

public class LootrunConfig {
    @Expose
    @ConfigOption(name = "Show Beacon Overlay", desc = "Count of every beacon you have taken in your run")
    @ConfigEditorBoolean
    public boolean showBeaconCountsOverlay = false;

    @Expose
    public HudElementData beaconCountsOverlay = new HudElementData(
        "Beacon Overlay",
                10,
                20,
                1.0f
    );

    @Expose
    @ConfigOption(name = "Show End Rewards Overlay", desc = "shows an overlay with your end rolls, sacs, pull, and effective pulls")
    @ConfigEditorBoolean
    public boolean showEndRewardsOverlay = false;

    @Expose
    public HudElementData endRewardsOverlay = new HudElementData(
            "End Rewards Overlay",
            10,
            30,
            1.0f
    );

    @Expose
    @ConfigOption(name = "Show Mission", desc = "shows an overlay with your active missions and trials")
    @ConfigEditorBoolean
    public boolean showMissionOverlay = false;

    @Expose
    public HudElementData missionOverlay = new HudElementData(
            "End Rewards Overlay",
            10,
            30,
            1.0f
    );
}
