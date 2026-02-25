package sidly.wynnadhoc.config.catagories;

import com.google.gson.annotations.Expose;
import com.wynntils.models.gear.type.GearTier;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class OuterVoidConfig {
    @Expose
    @ConfigOption(name = "Main Toggle", desc = "toggle all out void helper settings")
    @ConfigEditorBoolean
    public boolean mainToggle = false;

    @Expose
    @ConfigOption(name = "Line Rarity", desc = "at what rarity should lines be drawn")
    @ConfigEditorDropdown
    public GearTier showLinesAtRarity = GearTier.LEGENDARY;

    @Expose
    @ConfigOption(name = "Box Rarity", desc = "at what rarity should item hitboxes be drawn")
    @ConfigEditorDropdown
    public GearTier showBoxesAtRarity = GearTier.RARE;

    @Expose
    @ConfigOption(name = "Rare Priority", desc = "the distance of rare items is multiplied by this number for pathfinding \nlower is higher priority")
    @ConfigEditorSlider(minValue = 0.1F, maxValue = 2.0F, minStep = 0.05F)
    public float rareItemDistanceMultiplier = 0.65F;

    @Expose
    @ConfigOption(name = "Double Jump", desc = "do you have the Voidquartz Propulsor?")
    @ConfigEditorBoolean
    public boolean voidquartzPropulsor = false;
}