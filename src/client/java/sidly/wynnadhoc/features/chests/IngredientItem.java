package sidly.wynnadhoc.features.chests;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import sidly.wynnadhoc.utils.BitUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record IngredientItem(int tier, int lvl) implements EncodableItem {
    private static final Pattern ING_LVL_REGEX = Pattern.compile("\\D(\\d+) Crafting Level");
    private static final Pattern ING_TIER_REGEX = Pattern.compile("profession_tier_([0123])");

    public static IngredientItem fromItem(ItemStack itemStack) {
        if (!Items.POTION.equals(itemStack.getItem())) return null;
        if (itemStack.getCount() != 1) return null;
        LoreComponent lore = itemStack.getComponents().get(DataComponentTypes.LORE);
        if (lore == null) return null;

        int tier = -1;
        CustomModelDataComponent customModelDataComponent = itemStack.getComponents().get(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (customModelDataComponent == null) return null;
        for (String string : customModelDataComponent.strings()) {
            Matcher rarityMatch = ING_TIER_REGEX.matcher(string);
            if (!rarityMatch.matches()) continue;
            tier = Integer.parseInt(rarityMatch.group(1));
            break;
        }

        int level = -1;
        for (Text text : lore.lines()) {
            Matcher lvlLine = ING_LVL_REGEX.matcher(text.getString());
            if (!lvlLine.matches()) continue;
            level = Integer.parseInt(lvlLine.group(1));
            break;
        }

        if (tier != -1 && level != -1) {
            return new IngredientItem(tier, level);
        }
        return null;
    }

    public static IngredientItem decode(char encoded) {
        int tier = BitUtils.getBits(encoded, 2, 3);
        int lvl = BitUtils.getBits(encoded, 5, 8);
        return new IngredientItem(tier, lvl);
    }

    @Override
    public char encode() {
        char result = 0;
        result = BitUtils.setBits(result, 0, 2, 1);
        result = BitUtils.setBits(result, 2, 3, tier);
        result = BitUtils.setBits(result, 5, 8, lvl);
        return result;
    }
}
