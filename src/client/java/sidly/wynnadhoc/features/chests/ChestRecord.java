package sidly.wynnadhoc.features.chests;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.utils.FormatUtils;
import sidly.wynnadhoc.utils.ItemUtils;
import sidly.wynnadhoc.utils.datatypes.LevelRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record ChestRecord(BlockPos pos, List<ItemStack> items) {
    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("x", pos.getX());
        obj.addProperty("y", pos.getY());
        obj.addProperty("z", pos.getZ());

        JsonArray arr = new JsonArray();
        for (ItemStack item : items) {
            JsonElement itemJson = ConfigManager.GSON.toJsonTree(item);
            arr.add(itemJson);
        }
        obj.add("items", arr);
        return obj;
    }

    public static ChestRecord fromJson(JsonObject obj) {
        int x = obj.get("x").getAsInt();
        int y = obj.get("y").getAsInt();
        int z = obj.get("z").getAsInt();
        List<ItemStack> items = new ArrayList<>();

        if (obj.has("items")) {
            for (JsonElement e : obj.getAsJsonArray("items")) {
                if (e.isJsonObject()) {
                    items.add(ConfigManager.GSON.fromJson(e, ItemStack.class));
                }
            }
        }

        return new ChestRecord(new BlockPos(x, y, z), items);
    }

    public Map<LevelRange, Integer> getLvlQuantities() {
        Map<LevelRange, Integer> results = new HashMap<>();
        Pattern lvlRangePattern = Pattern.compile("Lv\\. Range: (\\d+)-(\\d+)");
        for (ItemStack item : items) {
            List<Text> fullTooltip = ItemUtils.getTooltip(item);
            StringBuilder sb = new StringBuilder();
            for (Text t : fullTooltip) {
                sb.append(t);
            }
            String tooltip = FormatUtils.removeColorCodes(sb.toString());
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
