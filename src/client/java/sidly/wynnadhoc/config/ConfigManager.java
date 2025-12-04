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
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Path;

public class ConfigManager {
    public static final Config INSTANCE = new Config();
    public static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("sidly/WynnAdhoc_config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static Screen getConfigScreen(Screen parent) {

        MoulConfigProcessor<Config> processor = new MoulConfigProcessor<>(ConfigManager.INSTANCE);

        // Register builtin editors so MoulConfig knows how to render annotated fields
        BuiltinMoulConfigGuis.addProcessors(processor);

        // Use the driver to process the structure (this will finish/finalize the processor)
        ConfigProcessorDriver driver = new ConfigProcessorDriver(processor);
        driver.processConfig(ConfigManager.INSTANCE); // this discovers @Category / @ConfigOption fields etc.

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

}
