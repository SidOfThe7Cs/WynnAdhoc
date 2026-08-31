package sidly.wynnadhoc.config.catagories

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class TnaConfig {
    @Expose
    @JvmField
    @ConfigOption(name = "Box Shadowlings", desc = "Draws a box around shadowlings in room 2")
    @ConfigEditorBoolean
    var boxShadowlings = false

    @Expose
    @JvmField
    @ConfigOption(
        name = "Draw Arrow",
        desc = "Draws a arc that rotates around your cursor pointing toward each shadowling"
    )
    @ConfigEditorBoolean
    var renderArrowPointer = false
}