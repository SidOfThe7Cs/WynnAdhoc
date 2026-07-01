package sidly.wynnadhoc.config.catagories;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.ChromaColour;
import io.github.notenoughupdates.moulconfig.annotations.*;
import sidly.wynnadhoc.features.chests.ChestDataDisplayOption;
import sidly.wynnadhoc.features.chests.ChestTier;

import java.util.List;

public class ChestConfig {
    @Expose
    @ConfigOption(name = "Highlight Chests", desc = "Draws outline of known nearby chests")
    @ConfigEditorBoolean
    public boolean forceEsp = false;

    @Expose
    @ConfigOption(name = "Ready Color", desc = "color when over 3d since last open")
    @ConfigEditorColour
    public String readyColor = ChromaColour.special(0, 255, 0, 255, 0);

    @Expose
    @ConfigOption(name = "Openable Color", desc = "color when over 30m since last open")
    @ConfigEditorColour
    public String openableColor = ChromaColour.special(0, 255, 255, 255, 0);

    @Expose
    @ConfigOption(name = "Cooldown Color", desc = "color when less than 3om since last open")
    @ConfigEditorColour
    public String cdColor = ChromaColour.special(0, 255, 255, 0, 0);

    @Expose
    @ConfigOption(name = "Only Openable", desc = "only show openable chests")
    @ConfigEditorBoolean
    public boolean onlyOpenable = true;

    @Expose
    @ConfigOption(name = "Only Tier", desc = "the tiers to show")
    @ConfigEditorDraggableList
    public List<ChestTier> shownTiers = Lists.newArrayList(ChestTier.values());

    @Expose
    @ConfigOption(name = "Max Distance", desc = "Max distance to show chests")
    @ConfigEditorSlider(minValue = 0.0F, maxValue = 1000.0F, minStep = 5.0F)
    public double maxEspDistance = 150.0F;

    @Expose
    @ConfigOption(name = "Track Chests", desc = "Track all the items you get from loot chests")
    @ConfigEditorBoolean
    public boolean trackChests = true;

    @Expose
    @ConfigOption(name = "Display Data", desc = "quick toggle for the below selector")
    @ConfigEditorBoolean
    public boolean displayLevel = false;

    @Expose
    @ConfigOption(name = "Display Data", desc = "what data should be displayed on the chest (do note order matters)")
    @ConfigEditorDraggableList
    public List<ChestDataDisplayOption> chestDisplayOptions = Lists.newArrayList(
            ChestDataDisplayOption.LOCAL_ING_COUNT,
            ChestDataDisplayOption.LOCAL_ITEM_COUNT,
            ChestDataDisplayOption.GLOBAL_ITEM_PERCENTS
    );

    @Expose
    @ConfigOption(name = "Sync Chests", desc = "Syncs chest locations and items between all users with this setting on")
    @ConfigEditorBoolean
    public boolean syncChests = true;

}
