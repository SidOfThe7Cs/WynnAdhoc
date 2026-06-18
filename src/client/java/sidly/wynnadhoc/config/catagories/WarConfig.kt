package sidly.wynnadhoc.config.catagories

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import sidly.wynnadhoc.config.gui.HudElementData

class WarConfig {
    @Expose
    @JvmField
    @ConfigOption(
        name = "Show War Resource Overlay",
        desc = "This doesnt work since the map menu was added to wynntills"
    )
    @ConfigEditorBoolean
    var showResourceOverlay: Boolean = false

    @Expose
    @JvmField
    var resourceOverlay: HudElementData = HudElementData(
        "War Resource Overlay",
        0.5f,
        0.2f,
        1.0f
    )

    @Expose
    @JvmField
    @ConfigOption(
        name = "Show War Timer",
        desc = "Shows the time until a war starts based off chat messages (so not accurate)"
    )
    @ConfigEditorBoolean
    var showWarTimer: Boolean = false

    @Expose
    @JvmField
    var warTimer: HudElementData = HudElementData(
        "War Timer",
        0.5f,
        0.4f,
        1.0f
    )
}