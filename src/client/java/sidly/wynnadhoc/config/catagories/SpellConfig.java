package sidly.wynnadhoc.config.catagories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorKeybind;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import org.lwjgl.glfw.GLFW;

public class SpellConfig {

    @Expose
    @ConfigOption(name = "Cast First Spell", desc = "spin attack - etc")
    @ConfigEditorKeybind(defaultKey = GLFW.GLFW_KEY_UNKNOWN)
    public int firstSpellCast = GLFW.GLFW_KEY_UNKNOWN;

    @Expose
    @ConfigOption(name = "Cast Second Spell", desc = "Dash - etc")
    @ConfigEditorKeybind(defaultKey = GLFW.GLFW_KEY_UNKNOWN)
    public int secondSpellCast = GLFW.GLFW_KEY_UNKNOWN;
}
