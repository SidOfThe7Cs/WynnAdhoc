package sidly.wynnadhoc.config.catagories

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class ShamanConfig {
    @Expose
    @JvmField
    @ConfigOption(name = "Totem Duration Warning", desc = "Main toggle for totem warning")
    @ConfigEditorBoolean
    var totemWarningToggle = false

    @Expose
    @JvmField
    @ConfigOption(name = "Duration", desc = "How many seconds should be remaining when warn")
    @ConfigEditorSlider(minValue = 1.0F, maxValue = 40.0F, minStep = 1.0F)
    var durationToWarn = 10

    @Expose
    @JvmField
    @ConfigOption(name = "Warn Every Second", desc = "Warns every second when lower than duration instead of only once")
    @ConfigEditorBoolean
    var repeatWarn = true

    @Expose
    @JvmField
    @ConfigOption(name = "Show Title", desc = "Show title when warn")
    @ConfigEditorBoolean
    var showTitle = false

    @Expose
    @JvmField
    @ConfigOption(name = "Play Sound", desc = "Play bell sound when warn")
    @ConfigEditorBoolean
    var playSound = true

    @Expose
    @JvmField
    @ConfigOption(name = "Volume", desc = "Volume")
    @ConfigEditorSlider(minValue = 0.0F, maxValue = 2.0F, minStep = 0.1F)
    var volume = 2.0F
}
