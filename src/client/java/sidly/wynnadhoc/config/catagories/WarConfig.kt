package sidly.wynnadhoc.config.catagories

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import sidly.wynnadhoc.config.gui.HudComponentData

class WarConfig {
    @Expose
    @JvmField
    @ConfigOption(name = "Show War Resource Overlay", desc = "<-")
    @ConfigEditorBoolean
    var showResourceOverlay: Boolean = true

    @Expose
    @JvmField
    var resourceOverlay: HudComponentData = HudComponentData(
        "War Resource Overlay",
        0.5f,
        0.2f,
        1.0f
    )

    @Expose
    @JvmField
    @ConfigOption(name = "Show War Timer", desc = "<-")
    @ConfigEditorBoolean
    var showWarTimer: Boolean = true

    @Expose
    @JvmField
    var warTimer: HudComponentData = HudComponentData(
        "War Timer",
        0.5f,
        0.4f,
        1.0f
    )
}