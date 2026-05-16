package sidly.wynnadhoc.config.catagories;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import com.wynntils.models.spells.type.SpellType;
import io.github.notenoughupdates.moulconfig.annotations.*;
import org.lwjgl.glfw.GLFW;
import sidly.wynnadhoc.config.gui.HudElementData;
import sidly.wynnadhoc.features.SpellMacros;

import java.util.List;

import static com.wynntils.models.spells.type.SpellType.*;

public class SpellConfig {

    @Expose
    @ConfigOption(name = "Toggle Spell Caster", desc = "main toggle for spellcaster\n(mainly for main attack shenanigans)")
    @ConfigEditorBoolean
    public boolean toggleSpellcaster = false;

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
    public boolean showQueueDisplay = false;

    @Expose
    public HudElementData spellQueueOverlay = new HudElementData(
            "Spell Queue",
            0.7f,
            0.05f,
            1.0f
    );

    @Expose
    @ConfigOption(name = "Min Timeout", desc = "time in milliseconds to retry if nothing was detected\nNote: setting this to low will break everything while setting it to high will only cause delays when holding main attack")
    @ConfigEditorSlider(minValue = 0.0F, maxValue = 1500.0F, minStep = 5.0F)
    public int minTimeout = 500;

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

    @Expose
    @ConfigOption(name = "Clear Queue", desc = "clears the current spell queue (in case something goes wrong)")
    @ConfigEditorButton(buttonText = "Clear Queue")
    public Runnable clearQueueButton = SpellMacros::clearQueue;

}
