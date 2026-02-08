package sidly.wynnadhoc.config;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.Category;
import sidly.wynnadhoc.config.catagories.GuiConfig;
import sidly.wynnadhoc.config.catagories.WarConfig;

public class Config extends io.github.notenoughupdates.moulconfig.Config {
    @Override
    public void saveNow() {
        ConfigManager.INSTANCE.save();
    }

    @Expose
    @Category(name = "Gui Settings", desc = "gui settings")
    public GuiConfig gui = new GuiConfig();

    @Expose
    @Category(name = "War Settings", desc = "war settings")
    public WarConfig war = new WarConfig();
}

