package sidly.wynnadhoc.config.catagories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorKeybind;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import org.lwjgl.glfw.GLFW;

public class PingConfig {

    @Expose
    @ConfigOption(name = "Wynnmod Ping Keybind", desc = "Press this key send a wynnmod ping to chat")
    @ConfigEditorKeybind(defaultKey = GLFW.GLFW_KEY_UNKNOWN)
    public int pingKeybind = GLFW.GLFW_KEY_UNKNOWN;

    @Expose
    @ConfigOption(name = "Render Wynnmod Pings", desc = "Render a thingy when someone sends a ping in chat")
    @ConfigEditorBoolean
    public boolean renderWynnmodPings = false;

    @Expose
    @ConfigOption(name = "Ping Duration", desc = "How many seconds should the pings stay there")
    @ConfigEditorSlider(minValue = 0.f, maxValue = 500.f, minStep = 1.f)
    public double pingDuration = 60;
}
