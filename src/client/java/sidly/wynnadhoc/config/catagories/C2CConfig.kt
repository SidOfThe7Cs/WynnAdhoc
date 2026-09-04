package sidly.wynnadhoc.config.catagories

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class C2CConfig {
    @Expose
    @JvmField
    @ConfigOption(
        name = "Allow C2C",
        desc = "Sends encrypted packets between players via /msg"
    )
    @ConfigEditorBoolean
    var allowC2C = true
}
