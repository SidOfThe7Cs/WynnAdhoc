package sidly.wynnadhoc.config.catagories;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;

public class SimpleFeatureToggles {
    @Expose
    @ConfigOption(name = "Bow Spammer", desc = "sends a right click packet every 5 ticks when holding right click with a bow")
    @ConfigEditorBoolean
    public boolean bowSpammerToggle = true;
}
