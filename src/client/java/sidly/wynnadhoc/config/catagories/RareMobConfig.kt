package sidly.wynnadhoc.config.catagories

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class RareMobConfig {
    @Expose
    @JvmField
    @ConfigOption(name = "Main Toggle", desc = "Turning this off will disable everything else here")
    @ConfigEditorBoolean
    var mainToggle = false

    @Expose
    @JvmField
    @ConfigOption(name = "Box Rare Mobs", desc = "Draws a box around rare mobs")
    @ConfigEditorBoolean
    var boxRareMobs = true

    @Expose
    @JvmField
    @ConfigOption(
        name = "Draw Arrow",
        desc = "Draws a arc that rotates around your cursor pointing toward each rare mob"
    )
    @ConfigEditorBoolean
    var renderArrowPointer = true

    @Expose
    @JvmField
    @ConfigOption(
        name = "Send chat message",
        desc = "Sends a client chat message with name and coords when a rare mob spawns"
    )
    @ConfigEditorBoolean
    var chatMsg = true

    @Expose
    @JvmField
    @ConfigOption(name = "Play Sound", desc = "Play wither spawn sound when a rare mob spawn")
    @ConfigEditorBoolean
    var playSound = true

    @Expose
    @JvmField
    @ConfigOption(
        name = "Volume",
        desc = "Volume of spawning sound (i dont think this does anything atleast not above 1)"
    )
    @ConfigEditorSlider(minValue = 0f, maxValue = 5f, minStep = 0.2f)
    var volume = 2f

    @Expose
    @JvmField
    @ConfigOption(
        name = "Cache Duration",
        desc = "How long in seconds should a rare mob still render after you see it after breaking line of sight, refreshed every time you see it"
    )
    @ConfigEditorSlider(minValue = 0f, maxValue = 30f, minStep = 1f)
    var cacheDuration = 7
}