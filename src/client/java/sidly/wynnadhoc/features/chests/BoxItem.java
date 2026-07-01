package sidly.wynnadhoc.features.chests;

import com.wynntils.models.gear.type.GearTier;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import sidly.wynnadhoc.utils.BitUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record BoxItem(GearTier rarity, int minLvl) implements EncodableItem {
    private static final Pattern LVL_RANGE_REGEX = Pattern.compile("(\\d+)-(\\d+) Level Range");
    private static final Pattern RARITY_REGEX = Pattern.compile("item_tier_(unique|rare|legendary|fabled|mythic)");

    public static BoxItem fromItem(ItemStack itemStack) {
        if (!Items.POTION.equals(itemStack.getItem())) return null;
        if (itemStack.getCount() != 1) return null;
        if (itemStack.getCustomName() == null) return null;
        boolean unIdentified = itemStack.getCustomName().getSiblings().stream().anyMatch(s -> s.getString().startsWith("Unidentified"));
        if (!unIdentified) return null;
        LoreComponent lore = itemStack.getComponents().get(DataComponentTypes.LORE);
        if (lore == null) return null;

        int minLvl = -1;
        for (Text text : lore.lines()) {
            Matcher lvlLine = LVL_RANGE_REGEX.matcher(text.getString());
            if (!lvlLine.matches()) continue;
            minLvl = Integer.parseInt(lvlLine.group(1));
            break;
        }

        GearTier rarity = null;
        CustomModelDataComponent customModelDataComponent = itemStack.getComponents().get(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (customModelDataComponent == null) return null;
        for (String string : customModelDataComponent.strings()) {
            Matcher rarityMatch = RARITY_REGEX.matcher(string);
            if (!rarityMatch.matches()) continue;
            rarity = GearTier.fromString(rarityMatch.group(1));
            break;
        }

        if (rarity != null && minLvl != -1) {
            return new BoxItem(rarity, minLvl);
        }
        return null;
    }

    public static BoxItem decode(char encoded) {
        int rarityOrdinal = BitUtils.getBits(encoded, 2, 3);
        int minLvl = BitUtils.getBits(encoded, 5, 8);
        return new BoxItem(GearTier.values()[rarityOrdinal], minLvl);
    }

    @Override
    public char encode() {
        char result = 0;
        // index 0 stays as 0 to indicate its a box and not an ing
        result = BitUtils.setBits(result, 2, 3, rarity.ordinal());
        result = BitUtils.setBits(result, 5, 8, minLvl);
        return result;
    }
}
