package sidly.wynnadhoc.config.catagories;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDraggableList;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import sidly.wynnadhoc.features.chests.ChestColor;
import sidly.wynnadhoc.features.chests.ChestTier;

import java.util.List;

public class ChestConfig {
    // TODO esp accordion
    @Expose
    @ConfigOption(name = "Force Chest Esp", desc = "Draws outline of known nearby chests")
    @ConfigEditorBoolean
    public boolean forceEsp = false;

    @Expose
    @ConfigOption(name = "Only Color", desc = "The colors chest esp should show, green is > 3d, yellow is > 30m, red is < 30m")
    @ConfigEditorDraggableList
    public List<ChestColor> shownColors = Lists.newArrayList(ChestColor.GREEN, ChestColor.YELLOW);

    @Expose
    @ConfigOption(name = "Only Tier", desc = "the tiers chest esp should show")
    @ConfigEditorDraggableList
    public List<ChestTier> shownTiers = Lists.newArrayList(ChestTier.values());

    @Expose
    @ConfigOption(name = "Max Distance", desc = "Max distance to show chest esp")
    @ConfigEditorSlider(minValue = 0.0F, maxValue = 1000.0F, minStep = 5.0F)
    public double maxEspDistance = 150.0F;

    @Expose
    @ConfigOption(name = "Track Chests", desc = "Track all the items you get from loot chests")
    @ConfigEditorBoolean
    public boolean trackChests = true;

    @Expose
    @ConfigOption(name = "Display Level", desc = "Display the % of the items you have gotten from a chest that are in each level range")
    @ConfigEditorBoolean
    public boolean displayLevel = false;
}
