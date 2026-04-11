package sidly.wynnadhoc.utils;

import com.wynntils.core.components.Models;
import com.wynntils.models.gear.type.GearTier;
import com.wynntils.models.items.properties.GearTierItemProperty;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemUtils {
    public static List<Text> getTooltip(ItemStack item) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return new ArrayList<>();

        return item.getTooltip(Item.TooltipContext.DEFAULT, client.player, TooltipType.BASIC);

    }

    public static List<Text> getLore(ItemStack itemStack) {
        return itemStack.getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines();
    }

    // TODO idk if this works now or if only on fruma and what wynntills version
    public static GearTier getItemRarity(ItemStack itemStack) {
        Optional<GearTierItemProperty> tieredItem = Models.Item.asWynnItemProperty(itemStack, GearTierItemProperty.class);
        return tieredItem.map(GearTierItemProperty::getGearTier).orElse(null);
    }

    public static Integer getEmeraldPouchTier(ItemStack itemStack) {
        List<Text> tooltip = ItemUtils.getTooltip(itemStack);
        if (tooltip == null || tooltip.isEmpty()) return null;
        String title = tooltip.getFirst().getString().trim().toLowerCase();
        if (!title.contains("emerald pouch")) return null;

        // Match either [Tier 8] or [Tier VIII]
        Pattern pattern = Pattern.compile("tier\\s+([ivx]+|\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(title);
        if (matcher.find()) {
            String tierStr = matcher.group(1).toUpperCase();

            // Try numeric first
            try {
                int tier = Integer.parseInt(tierStr);
                return (tier >= 1 && tier <= 10) ? tier : null;
            } catch (NumberFormatException ignored) {
            }

            // Convert Roman numeral to number
            int tier = FormatUtils.romanToInt(tierStr);
            return (tier >= 1 && tier <= 10) ? tier : null;
        }

        return null;
    }

    public static Float getFirsCustomModelDataFloat(ItemStack itemStack) {
        CustomModelDataComponent modelData = itemStack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (modelData == null) return null;
        List<Float> floats = modelData.floats();
        if (floats == null || floats.isEmpty()) return null;
        return floats.getFirst();
    }
}
