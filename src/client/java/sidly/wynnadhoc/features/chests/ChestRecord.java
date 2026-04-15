package sidly.wynnadhoc.features.chests;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import sidly.wynnadhoc.utils.FormatUtils;
import sidly.wynnadhoc.utils.datatypes.LevelRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChestRecord {
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
