package sidly.wynnadhoc.features.chests;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import sidly.wynnadhoc.utils.ItemUtils;

import java.util.List;

public class ChestLootItem {
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
