package sidly.wynnadhoc.features.chests;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wynntils.core.components.Models;
import com.wynntils.models.containers.containers.reward.ChallengeRewardContainer;
import com.wynntils.models.containers.containers.reward.LootChestContainer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.SegmentedSaveManager;
import sidly.wynnadhoc.config.catagories.ChestConfig;
import sidly.wynnadhoc.event.ChestItemsLoadedEvent;
import sidly.wynnadhoc.utils.FormatUtils;
import sidly.wynnadhoc.utils.ItemUtils;
import sidly.wynnadhoc.utils.LocationUtils;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChestTracker {
    public static File saveFolder = Paths.get("config", "sidly/chest_loot_data").toFile();
    public static SegmentedSaveManager saveManager = new SegmentedSaveManager(saveFolder, 100000, 1000000);
    public static List<ChestRecord> currentLoaded = loadLatest();
    private static ChestConfig config() { return ConfigManager.INSTANCE.config.chest; }

    private static BlockPos lastClickedChest = null;

    public static ActionResult onEntityClicked(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        if (entityHitResult == null) return ActionResult.PASS;
        if(entityHitResult.getEntity() instanceof InteractionEntity interactionEntity) {
            BlockPos pos = BlockPos.ofFloored(interactionEntity.getX(), interactionEntity.getY(), interactionEntity.getZ());
            Block block = world.getBlockState(pos).getBlock();
            if (block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST) {
                lastClickedChest = pos;
                return ActionResult.PASS;
            }
        }
        return ActionResult.PASS;
    }

    public static void onChestItemsLoaded(ChestItemsLoadedEvent event) {
        if (Models.Container.getCurrentContainer() instanceof LootChestContainer && config().trackChests) {

            if (lastClickedChest == null) return;

            List<ItemStack> items = event.getItems();
            ChestRecord record = new ChestRecord(lastClickedChest, items);

            currentLoaded.add(record);
            saveLatest();
        }
    }

    private static void saveLatest() {
        JsonArray array = new JsonArray();
        for (ChestRecord record : currentLoaded) {
            array.add(record.toJson());
        }

        boolean rolledOver = saveManager.save(array);
        if (rolledOver) currentLoaded.clear();
    }

    /** Loads the latest chest record data from disk */
    private static List<ChestRecord> loadLatest() {
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

        if (!saveFolder.exists() || !saveFolder.isDirectory()) return results;

        // Get all archived files + latest
        List<File> allFiles = new ArrayList<>(saveManager.listArchivedFiles());
        File latest = new File(saveFolder, "latest.json");
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
                ex.printStackTrace();
            }
        }

        return results;
    }


    public static class ChestRecord {
        public final BlockPos pos;
        public final List<ChestLootItem> items;

        public ChestRecord(BlockPos pos, List<ChestLootItem> items, boolean bruh) {
            this.pos = pos;
            this.items = items;
        }

        public ChestRecord(BlockPos pos, List<ItemStack> items) {
            this.pos = pos;
            this.items = items.stream().map(ChestLootItem::new).toList();
        }

        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("x", pos.getX());
            obj.addProperty("y", pos.getY());
            obj.addProperty("z", pos.getZ());

            JsonArray arr = new JsonArray();
            for (ChestLootItem item : items) {
                if (!item.tooltip.equals("Air\n")) {
                    arr.add(item.toJson());
                }
            }
            obj.add("items", arr);
            return obj;
        }

        public static ChestRecord fromJson(JsonObject obj) {
            int x = obj.get("x").getAsInt();
            int y = obj.get("y").getAsInt();
            int z = obj.get("z").getAsInt();
            List<ChestLootItem> items = new ArrayList<>();

            if (obj.has("items")) {
                for (JsonElement e : obj.getAsJsonArray("items")) {
                    if (e.isJsonObject()) {
                        items.add(ChestLootItem.fromJson(e.getAsJsonObject()));
                    }
                }
            }

            return new ChestRecord(new BlockPos(x, y, z), items, true);
        }

        public Map<LevelRange, Integer> getLvlQuantities() {
            Map<LevelRange, Integer> results = new HashMap<>();
            Pattern lvlRangePattern = Pattern.compile("Lv\\. Range: (\\d+)-(\\d+)");
            for (ChestLootItem item : items) {
                String tooltip = FormatUtils.removeColorCodes(item.tooltip);
                Matcher matcher = lvlRangePattern.matcher(tooltip);
                if (matcher.find()) {
                    int min = Integer.parseInt(matcher.group(1));
                    int max = Integer.parseInt(matcher.group(2));
                    LevelRange levelRange = new LevelRange(min, max);
                    results.merge(levelRange, 1, Integer::sum);
                }
            }
            return results;
        }
    }

    public static class LevelRange {
        private final int min;
        private final int max;

        public LevelRange(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public String toString() {
            return min + " - " + max;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            LevelRange that = (LevelRange) object;
            return min == that.min && max == that.max;
        }

        @Override
        public int hashCode() {
            return Objects.hash(min, max);
        }
    }

    public static class ChestLootItem {
        public String tooltip;

        public ChestLootItem(ItemStack itemStack) {
            StringBuilder sb = new StringBuilder();
            List<Text> tooltipText = ItemUtils.getTooltip(itemStack);
            for (Text text : tooltipText) {
                sb.append(text.getString()).append("\n");
            }
            tooltip = sb.toString();
        }
        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("tooltip", tooltip);
            return obj;
        }

        public static ChestLootItem fromJson(JsonObject obj) {
            ChestLootItem item = new ChestLootItem(ItemStack.EMPTY);
            item.tooltip = obj.has("tooltip") ? obj.get("tooltip").getAsString() : "";
            return item;
        }
    }

    // TODO make draggable list with even more options like possible mythics
    public static void onTextDisplaySync(DisplayEntity.TextDisplayEntity textDisplay) {
        String textDisplayText = textDisplay.getText().getString();
        if (textDisplayText.contains("Loot Chest") && config().displayLevel) {
            MutableText copy = textDisplay.getText().copy();
            BlockPos chestPos = LocationUtils.getBlockUnderVec3d(textDisplay.getEntityPos());
            List<ChestRecord> allRecordsForChest = getAllRecordsForChest(chestPos);
            Map<LevelRange, Integer> lvlQuantities = new HashMap<>();
            for (ChestRecord record : allRecordsForChest) {
                Map<LevelRange, Integer> recordLvlQuantities = record.getLvlQuantities();
                recordLvlQuantities.forEach((key, value) -> lvlQuantities.merge(key, value, Integer::sum));
            }
            int total = lvlQuantities.values().stream().mapToInt(Integer::intValue).sum();
            copy.getSiblings().add(Text.of("\nBoxes Found: " + total));
            lvlQuantities.entrySet().stream()
                    .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                    .forEach(entry -> {
                        int percent = (int) ((entry.getValue() / (double) total) * 100);
                        copy.getSiblings().add(Text.of("\n" + entry.getKey() + " " + percent + "%"));
                    });

            textDisplay.setText(copy);
        }
    }
}
