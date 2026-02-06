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
import net.minecraft.util.math.BlockPos;
import sidly.wynnadhoc.lootruns.LootrunningSaveData;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    public static final ConfigManager INSTANCE = new ConfigManager();

    public Config config = new Config();  // the in-memory config
    private final File configFile = new File("config/sidly/wynnadhoc.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    // TODO actually save to
    private LootrunningSaveData lootrunSaveData = new LootrunningSaveData();

    public LootrunData getLootrun(String uuid) {
        return lootrunSaveData.lootruns.computeIfAbsent(uuid, k -> new LootrunData(new HashMap<>()));
    }
    public void resetLootrun(String uuid) {
        lootrunSaveData.lootruns.put(uuid, new LootrunData(lootrunSaveData.lootruns.get(uuid).getCampData())); // create new object but preserve camp data
    }
    public Map<BlockPos, Long> getChests() {
        return lootrunSaveData.chests;
    }
    public List<BlockPos> getBannedChests() {
        return lootrunSaveData.bannedChests;
    }

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
