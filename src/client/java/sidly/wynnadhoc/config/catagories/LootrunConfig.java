package sidly.wynnadhoc.config.catagories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import sidly.wynnadhoc.config.gui.HudComponentData;

public class LootrunConfig {
    @Expose
    @ConfigOption(name = "Show Beacon Overlay", desc = "Count of every beacon you have taken in your run")
    @ConfigEditorBoolean
    public boolean showBeaconCountsOverlay = false;

    @Expose
    public HudComponentData beaconCountsOverlay = new HudComponentData.Builder(
            "Beacon Overlay",
            0f,
            0.5f
    ).build();

    @Expose
    @ConfigOption(name = "Show End Rewards Overlay", desc = "shows an overlay with your end rolls, sacs, pull, and effective pulls")
    @ConfigEditorBoolean
    public boolean showEndRewardsOverlay = false;

    @Expose
    public HudComponentData endRewardsOverlay = new HudComponentData.Builder(
            "End Rewards Overlay",
            0.2f,
            0.2f
    ).build();

    @Expose
    @ConfigOption(name = "Show Mission", desc = "shows an overlay with your active missions and trials")
    @ConfigEditorBoolean
    public boolean showMissionOverlay = false;

    @Expose
    public HudComponentData missionOverlay = new HudComponentData.Builder(
            "End Rewards Overlay",
            0.6f,
            0.2f
    ).build();
}
