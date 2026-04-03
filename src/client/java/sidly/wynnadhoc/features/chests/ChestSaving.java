package sidly.wynnadhoc.features.chests;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.util.math.BlockPos;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.SegmentedSaveManager;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ChestSaving {
    public static File SAVE_FILE = ConfigManager.getConfigDir().resolve("chest_loot_data").toFile();
    public static SegmentedSaveManager saveManager = new SegmentedSaveManager(SAVE_FILE, 100000, 1000000);
    public static List<ChestRecord> currentLoaded = loadLatest();

    public static void saveLatest() {
        JsonArray array = new JsonArray();
        for (ChestRecord record : currentLoaded) {
            array.add(record.toJson());
        }

        boolean rolledOver = saveManager.save(array);
        if (rolledOver) currentLoaded.clear();
    }

    /**
     * Loads the latest chest record data from disk
     */
    public static List<ChestRecord> loadLatest() {
        JsonArray array = saveManager.loadLatest();
        List<ChestRecord> result = new ArrayList<>();
        for (JsonElement e : array) {
            if (e.isJsonObject()) {
                result.add(ChestRecord.fromJson(e.getAsJsonObject()));
            }
        }
        return result;
    }

    public static List<ChestRecord> getAllRecordsForChest(BlockPos pos) {
        List<ChestRecord> results = new ArrayList<>();

        if (!SAVE_FILE.exists() || !SAVE_FILE.isDirectory()) return results;

        // Get all archived files + latest
        List<File> allFiles = new ArrayList<>(saveManager.listArchivedFiles());
        File latest = new File(SAVE_FILE, "latest.json");
        if (latest.exists()) allFiles.add(latest);

        for (File file : allFiles) {
            try (FileReader reader = new FileReader(file)) {
                JsonElement element = JsonParser.parseReader(reader);
                if (!element.isJsonArray()) continue;

                JsonArray arr = element.getAsJsonArray();
                for (JsonElement e : arr) {
                    if (!e.isJsonObject()) continue;

                    ChestRecord record = ChestRecord.fromJson(e.getAsJsonObject());
                    if (record.pos.equals(pos)) {
                        results.add(record);
                    }
                }
            } catch (Exception ex) {
                WynnAdhocClient.LOGGER.warn(ex + "\n" + "Failed to chest load file " + file.getAbsolutePath());
            }
        }

        return results;
    }
}
