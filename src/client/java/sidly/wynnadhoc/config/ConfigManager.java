package sidly.wynnadhoc.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.notenoughupdates.moulconfig.gui.MoulConfigEditor;
import io.github.notenoughupdates.moulconfig.gui.GuiContext;
import io.github.notenoughupdates.moulconfig.gui.GuiElementComponent;
import io.github.notenoughupdates.moulconfig.platform.MoulConfigScreenComponent;
import io.github.notenoughupdates.moulconfig.processor.BuiltinMoulConfigGuis;
import io.github.notenoughupdates.moulconfig.processor.ConfigProcessorDriver;
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.File;

public class ConfigManager {
    public static final ConfigManager INSTANCE = new ConfigManager();

    public Config config = new Config();  // the in-memory config
    private final File configFile = new File("config/sidly/wynnadhoc.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Screen getConfigScreen(Screen parent) {

        MoulConfigProcessor<Config> processor = new MoulConfigProcessor<>(this.config);

        // Register builtin editors so MoulConfig knows how to render annotated fields
        BuiltinMoulConfigGuis.addProcessors(processor);

        // Use the driver to process the structure (this will finish/finalize the processor)
        ConfigProcessorDriver driver = new ConfigProcessorDriver(processor);
        driver.processConfig(this.config); // this discovers @Category / @ConfigOption fields etc.

        // Create the editor using the processed config
        MoulConfigEditor<Config> editor = new MoulConfigEditor<>(processor);

        // Wrap in the required GUI context
        MoulConfigScreenComponent screen = new MoulConfigScreenComponent(
                Text.literal("WynnAdhoc Config"),
                new GuiContext(new GuiElementComponent(editor)),
                parent
        );

        screen.getGuiContext().setCloseRequestHandler(() -> {
            //ConfigManager.save();
            MinecraftClient.getInstance().setScreen(parent);
        });

        return screen;
    }

    public void load() {
        configFile.getParentFile().mkdirs();
        Config loaded = ConfigUtil.loadConfig(Config.class, configFile, gson);

        if (loaded != null) {
            this.config = loaded;
        } else {
            // Config file missing or corrupted → regenerate defaults
            this.config = new Config();
            save();
        }
    }

    public void save() {
        ConfigUtil.saveConfig(this.config, configFile, gson);
    }


}
