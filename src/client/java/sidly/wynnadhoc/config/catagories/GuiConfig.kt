package sidly.wynnadhoc.config.catagories

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorButton
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorKeybind
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.config.gui.HudElementManager

class GuiConfig {
    @Expose
    @JvmField
    @ConfigOption(name = "Open Hud Editor", desc = "Press this key to open the hud editor from anywhere.")
    @ConfigEditorKeybind(defaultKey = GLFW.GLFW_KEY_UNKNOWN)
    var guiEditorKeybind: Int = GLFW.GLFW_KEY_UNKNOWN

    @Transient
    @JvmField
    @ConfigOption(name = "Open Hud Editor", desc = "Click to instantly open the hud editor.")
    @ConfigEditorButton(buttonText = "Click")
    var openEditorButton: Runnable = Runnable {
        ConfigManager.INSTANCE.save()
        MinecraftClient.getInstance().currentScreen?.close()
        HudElementManager.openEditor()
    }

    @Expose
    @JvmField
    @ConfigOption(name = "Default Line Width", desc = "<-")
    @ConfigEditorSlider(minValue = 0.1F, maxValue = 4.0F, minStep = 0.1F)
    var defaultLineWidth: Double = 2.0

    @Expose
    @JvmField
    @ConfigOption(name = "Line Distance Factor", desc = "The smaller the value the thicker farther lines are")
    @ConfigEditorSlider(minValue = 0.1F, maxValue = 2.0F, minStep = 0.1F)
    var lineDistanceFactor: Double = 1.0
}