package sidly.wynnadhoc.config.catagories;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import com.wynntils.models.spells.type.SpellType;
import io.github.notenoughupdates.moulconfig.annotations.*;
import org.lwjgl.glfw.GLFW;
import sidly.wynnadhoc.config.gui.HudElementData;

import java.util.List;

import static com.wynntils.models.spells.type.SpellType.*;

public class SpellConfig {

    @Expose
    @ConfigOption(name = "Cast First Spell", desc = "Arrow Storm, Totem, Bash, Spin Attack, Heal")
    @ConfigEditorKeybind(defaultKey = GLFW.GLFW_KEY_UNKNOWN)
    public int firstSpellCast = GLFW.GLFW_KEY_UNKNOWN;

    @Expose
    @ConfigOption(name = "Cast Second Spell", desc = "Escape, Haul, Charge, Dash, Teleport")
    @ConfigEditorKeybind(defaultKey = GLFW.GLFW_KEY_UNKNOWN)
    public int secondSpellCast = GLFW.GLFW_KEY_UNKNOWN;

    @Expose
    @ConfigOption(name = "Cast Third Spell", desc = "Arrow Bomb, Aura, Uppercut, Multihit, Meteor")
    @ConfigEditorKeybind(defaultKey = GLFW.GLFW_KEY_UNKNOWN)
    public int thirdSpellCast = GLFW.GLFW_KEY_UNKNOWN;

    @Expose
    @ConfigOption(name = "Cast Fourth Spell", desc = "Arrow Shield, Uproot, War Scream, Smoke Bomb, Ice Snake")
    @ConfigEditorKeybind(defaultKey = GLFW.GLFW_KEY_UNKNOWN)
    public int fourthSpellCast = GLFW.GLFW_KEY_UNKNOWN;

    @Expose
    @ConfigOption(name = "Queue Size", desc = "max amount of spells that can be in queue")
    @ConfigEditorSlider(minValue = 0.0F, maxValue = 2.0F, minStep = 1.0F)
    public int maxQueueSize = 2;

    @Expose
    @ConfigOption(name = "Queue Display", desc = "Display the spell queue as an overlay")
    @ConfigEditorBoolean
    public boolean showQueueDisplay = true;

    @Expose
    public HudElementData spellQueueOverlay = new HudElementData(
            "Spell Queue",
            0.7f,
            0.05f,
            1.0f
    );

    @Expose
    @ConfigOption(name = "Hold Cast Delay", desc = "amount of ticks to wait before spamming spell when holding a key")
    @ConfigEditorSlider(minValue = 0.0F, maxValue = 20.0F, minStep = 1.0F)
    public int holdToCastDelay = 10;

    @Expose
    @ConfigOption(name = "Miss-cast Priority", desc = "what spell should be cast if a spell cast is impossible")
    @ConfigEditorDraggableList
    public List<SpellType> misscastPrioList = Lists.newArrayList(
            ARROW_SHIELD,
            AURA,
            WAR_SCREAM,
            UPPERCUT,
            SPIN_ATTACK,
            HEAL
    );
}
