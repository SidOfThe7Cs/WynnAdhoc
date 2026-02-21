package sidly.wynnadhoc.config.catagories

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import sidly.wynnadhoc.config.gui.HudElementData

class WarConfig {
    @Expose
    @JvmField
    @ConfigOption(name = "Show War Resource Overlay", desc = "<-")
    @ConfigEditorBoolean
    var showResourceOverlay: Boolean = true

    @Expose
    @JvmField
    var resourceOverlay: HudElementData = HudElementData(
        "War Resource Overlay",
        10,
        10,
        1.0f
    )

    @Expose
    @JvmField
    @ConfigOption(name = "Show War Timer", desc = "<-")
    @ConfigEditorBoolean
    var showWarTimer: Boolean = true

    @Expose
    @JvmField
    var warTimer: HudElementData = HudElementData(
        "War Timer",
        10,
        40,
        1.0f
    )
}