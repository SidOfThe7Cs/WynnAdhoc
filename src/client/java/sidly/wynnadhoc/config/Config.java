package sidly.wynnadhoc.config;

import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.Category;
import sidly.wynnadhoc.config.catagories.*;

public class Config extends io.github.notenoughupdates.moulconfig.Config {
    @Override
    public void saveNow() {
        ConfigManager.INSTANCE.save();
    }

    @Expose
    @Category(name = "Gui Settings", desc = "gui settings")
    public GuiConfig gui = new GuiConfig();

    @Expose
    @Category(name = "Toggles", desc = "single feature toggles")
    public SimpleFeatureToggles toggles = new SimpleFeatureToggles();

    @Expose
    @Category(name = "War Settings", desc = "war settings")
    public WarConfig war = new WarConfig();

    @Expose
    @Category(name = "Outer Void Settings", desc = "outer void settings")
    public OuterVoidConfig outerVoid = new OuterVoidConfig();

    @Expose
    @Category(name = "Chest Related Settings", desc = "chest related settings")
    public ChestConfig chest = new ChestConfig();

    @Expose
    @Category(name = "Lootrun Settings", desc = "lootrun settings")
    public LootrunConfig lootrun = new LootrunConfig();
}

