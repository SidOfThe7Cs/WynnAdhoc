package sidly.wynnadhoc.config.catagories

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class ItemBoxConfig {
    @Expose
    @JvmField
    @ConfigOption(name = "Box Mythic Boxes", desc = "Draws a box around mythic item boxes on the ground")
    @ConfigEditorBoolean
    var boxMythics = false

    @Expose
    @JvmField
    @ConfigOption(
        name = "Draw Arrow",
        desc = "Draws a arc that rotates around your cursor pointing toward each mythic box"
    )
    @ConfigEditorBoolean
    var renderArrowPointerToMythics = false

    @Expose
    @JvmField
    @ConfigOption(
        name = "Send Chat Message",
        desc = "Sends a client chat message with coords when you drop a mythic box"
    )
    @ConfigEditorBoolean
    var chatMsgMythic = true

    @Expose
    @JvmField
    @ConfigOption(name = "Play Sound", desc = "Play mythic find sound when you drop a mythic box")
    @ConfigEditorBoolean
    var playSoundMythic = true
}
