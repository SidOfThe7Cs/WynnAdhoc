package sidly.wynnadhoc.config;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.notenoughupdates.moulconfig.gui.GuiContext;
import io.github.notenoughupdates.moulconfig.gui.GuiElementComponent;
import io.github.notenoughupdates.moulconfig.gui.MoulConfigEditor;
import io.github.notenoughupdates.moulconfig.platform.MoulConfigScreenComponent;
import io.github.notenoughupdates.moulconfig.processor.BuiltinMoulConfigGuis;
import io.github.notenoughupdates.moulconfig.processor.ConfigProcessorDriver;
import io.github.notenoughupdates.moulconfig.processor.MoulConfigProcessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.saves.*;
import sidly.wynnadhoc.features.chests.ChestTracker;
import sidly.wynnadhoc.features.chests.LootChest;
import sidly.wynnadhoc.features.lootruns.LootrunData;
import sidly.wynnadhoc.server.ChestCrowdsource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ConfigManager {
    public static final ConfigManager INSTANCE = new ConfigManager();
    public static final Gson DEFUALT_GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<BlockPos, ChestsSaveData.ChestData>>() {
                    }.getType(),
                    (JsonSerializer<Map<BlockPos, ChestsSaveData.ChestData>>) (src, typeOfSrc, context) -> {
                        JsonArray array = new JsonArray(); // We'll use an array of entries
                        for (Map.Entry<BlockPos, ChestsSaveData.ChestData> entry : src.entrySet()) {
                            JsonObject obj = new JsonObject();
                            JsonObject posObj = new JsonObject();
                            posObj.addProperty("x", entry.getKey().getX());
                            posObj.addProperty("y", entry.getKey().getY());
                            posObj.addProperty("z", entry.getKey().getZ());
                            obj.add("pos", posObj);
                            obj.add("value", DEFUALT_GSON.toJsonTree(entry.getValue()));
                            array.add(obj);
                        }
                        return array;
                    }
            )
            .registerTypeAdapter(new TypeToken<Map<BlockPos, ChestsSaveData.ChestData>>() {
                    }.getType(),
                    (JsonDeserializer<Map<BlockPos, ChestsSaveData.ChestData>>) (json, typeOfT, context) -> {
                        Map<BlockPos, ChestsSaveData.ChestData> map = new HashMap<>();
                        JsonArray array = json.getAsJsonArray();
                        for (JsonElement element : array) {
                            JsonObject obj = element.getAsJsonObject();
                            JsonObject posObj = obj.getAsJsonObject("pos");
                            int x = posObj.get("x").getAsInt();
                            int y = posObj.get("y").getAsInt();
                            int z = posObj.get("z").getAsInt();
                            JsonObject value = obj.get("value").getAsJsonObject();
                            map.put(new BlockPos(x, y, z), DEFUALT_GSON.fromJson(value, ChestsSaveData.ChestData.class));
                        }
                        return map;
                    }
            )
            .registerTypeAdapter(ItemStack.class, (JsonSerializer<ItemStack>) (src, typeOfSrc, context) -> {
                DataResult<JsonElement> result = ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, src);
                return result.result().orElse(null);
            })
            .registerTypeAdapter(ItemStack.class, (JsonDeserializer<ItemStack>) (json, typeOfT, context) -> {
                DataResult<Pair<ItemStack, JsonElement>> result = ItemStack.CODEC.decode(JsonOps.INSTANCE, json);
                Pair<ItemStack, JsonElement> pair = result.result().orElse(null);
                if (pair == null) return null;
                return pair.getFirst();
            })
            .setPrettyPrinting().create();
    private static Path CONFIG_DIR = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("sidly");

    private final File MAIN_CONFIG_FILE = getConfigDir().resolve("wynnadhoc.json").toFile();

    public static Path getConfigDir() {
        if (CONFIG_DIR == null) {
            CONFIG_DIR = FabricLoader.getInstance()
                    .getConfigDir()
                    .resolve("sidly");
        }
        return CONFIG_DIR;
    }

    public Config config = new Config();

    private LootrunSaveData lootrunSaveData;
    private ChestsSaveData chests;
    private SessionToken sessionToken;
    private LastVersion lastVersion;

    public LootrunData getLootrun(String uuid) {
        if (uuid.isEmpty()) {
            WynnAdhocClient.LOGGER.warn("got lootrun data while uuid was null this is not real data");
            return new LootrunData(new HashMap<>(), "");
        }
        return lootrunSaveData.lootruns.computeIfAbsent(uuid, k -> new LootrunData(new HashMap<>(), uuid));
    }

    public void resetLootrun(String uuid) {
        lootrunSaveData.lootruns.put(uuid, new LootrunData(lootrunSaveData.lootruns.get(uuid).getCampData(), uuid)); // create new object but preserve camp data
    }

    public Map<BlockPos, ChestsSaveData.ChestData> getChests() {
        return chests.chests;
    }

    public String getToken() {
        return sessionToken.token == null ? "" : sessionToken.token;
    }

    public String getLastVersion() {
        return lastVersion.lastVersion == null ? "0.0.0" : lastVersion.lastVersion;
    }

    public void setLastVersion(String newVersion) {
        this.lastVersion.lastVersion = newVersion;
        this.lastVersion.save();
    }

    public static CompletableFuture<Integer> loadChestsFromServer() {
        if (!INSTANCE.config.chest.syncChests) {
            ChestTracker.INSTANCE.cacheChestData(List.of());
            return CompletableFuture.completedFuture(0);
        }
        CompletableFuture<List<LootChest>> downloadedChests = ChestCrowdsource.getChests();
        return downloadedChests.thenApply((l) -> {
            ChestTracker.INSTANCE.cacheChestData(l);
            return l.size();
        });
    }

    public void storeToken(String sessionToken) {
        this.sessionToken.token = sessionToken;
        this.sessionToken.save();
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void load() {
        ConfigManager.INSTANCE.lootrunSaveData = new LootrunSaveData();
        ConfigManager.INSTANCE.chests = new ChestsSaveData();
        ConfigManager.INSTANCE.sessionToken = new SessionToken();
        ConfigManager.INSTANCE.lastVersion = new LastVersion();

        BasicSavable.loadAll();

        MAIN_CONFIG_FILE.getParentFile().mkdirs();
        try {
            MAIN_CONFIG_FILE.createNewFile();
        } catch (IOException e) {
            WynnAdhocClient.LOGGER.error("Could not create config file!");
            e.printStackTrace();
        }
        Config loaded = ConfigUtil.loadConfig(Config.class, MAIN_CONFIG_FILE, GSON);

        if (loaded != null) {
            this.config = loaded;
        } else {
            WynnAdhocClient.LOGGER.error("Config file could not be loaded");
            this.config = new Config();
        }

        loadChestsFromServer();
    }

    public void save() {
        BasicSavable.saveAll();
        ConfigUtil.saveConfig(this.config, MAIN_CONFIG_FILE, GSON);
    }
}
