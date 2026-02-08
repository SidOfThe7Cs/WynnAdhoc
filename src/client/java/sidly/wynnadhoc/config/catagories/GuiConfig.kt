package sidly.wynnadhoc.config.catagories

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorKeybind
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import org.lwjgl.glfw.GLFW

class GuiConfig {
    @Expose
    @ConfigOption(name = "Open Gui Editor", desc = "Press this key to transform hud elements.")
    @ConfigEditorKeybind(defaultKey = GLFW.GLFW_KEY_UNKNOWN)
    var guiEditorKeybind: Int = GLFW.GLFW_KEY_UNKNOWN
}