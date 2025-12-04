package sidly.wynnadhoc.config;

import io.github.notenoughupdates.moulconfig.annotations.Category;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import com.google.gson.annotations.Expose;

public class Config extends io.github.notenoughupdates.moulconfig.Config {
    @Override
    public void saveNow() {
        ConfigManager.INSTANCE.save();
    }

    @Expose
    @Category(name = "Main Settings", desc = "description for main settings")
    public MainSettings mainSettings = new MainSettings();

    public static class MainSettings {
        @Expose
        @ConfigOption(
                name = "Enable Feature",
                desc = "A simple toggle option."
        )
        @ConfigEditorBoolean
        public boolean enableFeature = false;

        @Expose
        @ConfigOption(
                name = "Dropdown Feature",
                desc = "A simple dropdown option."
        )
        @ConfigEditorDropdown
        public TestOptions dropdownFeature = TestOptions.Never;
    }

    public enum TestOptions{
        Always,
        Never,
        Maybe;
    }
}

