package sidly.wynnadhoc.features.chests;

import com.wynntils.features.inventory.ItemFavoriteFeature;
import com.wynntils.models.gear.type.GearTier;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.ChestConfig;
import sidly.wynnadhoc.event.ChestItemsLoadedEvent;
import sidly.wynnadhoc.mixin.client.Invoker.IsFavoritedInvoker;
import sidly.wynnadhoc.utils.Debug;
import sidly.wynnadhoc.utils.FormatUtils;
import sidly.wynnadhoc.utils.ItemUtils;
import sidly.wynnadhoc.utils.TickScheduler;
import sidly.wynnadhoc.utils.auto.AutoUtils;
import sidly.wynnadhoc.utils.auto.MouseLerper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoLootChests {
    private static ChestConfig config() {
        return ConfigManager.INSTANCE.config.chest;
    }

    public static TickScheduler chestLooterScheduler = TickScheduler.getNewScheduler();

    public static void onChestItemsLoaded(ChestItemsLoadedEvent event) {
        GenericContainerScreen screen = event.containerScreen;

        if (!config().autoCloseChests) {
            MouseLerper.cancelAll();
            return;
        }

        // challenge rewards are in a lootrun and are not the same as world event rewards
        if (event.isLootChest() || event.isChallengeReward() || event.isFlyingChest()) {
            chestLooterScheduler.schedule(2, 10, remaining -> {
                boolean hasFavorites = lootChest(screen);
                if (!hasFavorites) {
                    AutoUtils.closeScreen();
                    chestLooterScheduler.cancelAll();
                }
            }, true);
        }
    }

    // returns true if favorites were found in the chest
    private static boolean lootChest(GenericContainerScreen screen) {
        boolean result = false;
        int containerSlots = screen.getScreenHandler().slots.size() - 36; // inventory is 36 size
        for (int i = 0; i < containerSlots; i++) {
            Slot slot = screen.getScreenHandler().getSlot(i);
            ItemStack itemStack = slot.getStack();
            if (itemStack.isEmpty()) continue;

            // check itemRarity from wynntills
            GearTier itemRarity = ItemUtils.getItemRarity(itemStack);
            Integer pouchTier = ItemUtils.getEmeraldPouchTier(itemStack);
            if (itemRarity == GearTier.MYTHIC || (pouchTier != null && pouchTier >= 8)) {
                WynnAdhocClient.LOGGER.info(Debug.Type.LOOTRUN, "found mythic not looting chest");
                result = true;
                continue; // dont take the item out of the chest
            }

            // also check box rarity from own code
            EncodableItem encodableItem = EncodableItem.fromItem(itemStack);
            if (encodableItem instanceof BoxItem box) {
                if (box.rarity() == GearTier.MYTHIC) {
                    WynnAdhocClient.LOGGER.info(Debug.Type.LOOTRUN, "found mythic not looting chest");
                    result = true;
                    continue; // dont take the item out of the chest
                }
            }

            boolean isNeededPotion = false;
            if (itemStack.getName().getString().equals("Potion of Healing [3/3]")) {
                Map<Integer, Integer> hotBarPotions = getHotBarPotions(screen);
                int healAmount = -1;

                List<Text> tooltip = ItemUtils.getTooltip(itemStack);
                for (Text line : tooltip) {
                    String string = FormatUtils.removeColorCodes(line.getString());
                    Pattern pattern = Pattern.compile("Heal:\\s*(\\d+)");
                    Matcher matcher = pattern.matcher(string);

                    if (matcher.find()) {
                        healAmount = Integer.parseInt(matcher.group(1));
                    }
                }

                if (hotBarPotions != null && healAmount != -1) {
                    int finalHealAmount = healAmount;

                    // check if all potions that heal atleast as much as the current one total to less than 30
                    isNeededPotion = hotBarPotions.entrySet().stream()
                            .filter(e -> e.getKey() >= finalHealAmount)
                            .mapToInt(Map.Entry::getValue)
                            .sum() < 30;

                }
            }

            boolean favorite = ((IsFavoritedInvoker) new ItemFavoriteFeature()).invokeIsFavorited(itemStack);
            if (favorite || isNeededPotion) {
                AutoUtils.shiftClickSlot(screen, slot.getIndex());
                result = true;
            }
        }
        return result;
    }

    private static Map<Integer, Integer> getHotBarPotions(GenericContainerScreen screen) {
        ScreenHandler handler = screen.getScreenHandler();
        int size = handler.slots.size();
        ItemStack potions = null;

        for (int i = size - 9; i < size; i++) {
            Slot slot = handler.slots.get(i);
            ItemStack stack = slot.getStack();
            if (stack.getName().getString().contains("Potions of Healing")) {
                potions = stack;
            }
        }

        if (potions != null) {
            Map<Integer, Integer> result = new HashMap<>();
            Pattern pattern = Pattern.compile("\\+(\\d+).*?\\[(\\d+)/(\\d+)]");

            List<Text> tooltip = ItemUtils.getTooltip(potions);
            for (Text line : tooltip) {
                String string = FormatUtils.removeColorCodes(line.getString());
                Matcher matcher = pattern.matcher(string);
                if (matcher.find()) {
                    int healAmount = Integer.parseInt(matcher.group(1));
                    int max = Integer.parseInt(matcher.group(3));

                    result.put(healAmount, max);
                }
            }

            return result;
        } else return null;
    }

    private static String formatChestContents(GenericContainerScreen screen) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Chest Items Loaded ===\n");
        sb.append("Container: ").append(screen.getTitle().getString()).append("\n");

        int containerSlots = screen.getScreenHandler().slots.size() - 36; // inventory is 36 size
        for (int i = 0; i < containerSlots; i++) {
            boolean favorite = ((IsFavoritedInvoker) new ItemFavoriteFeature()).invokeIsFavorited(screen.getScreenHandler().slots.get(i).getStack());
            String itemName = screen.getScreenHandler().slots.get(i).getStack().isEmpty()
                    ? "Empty"
                    : screen.getScreenHandler().slots.get(i).getStack().getName().getString();

            sb.append(String.format("Slot %02d: %s%s\n", i, itemName, favorite ? " (Favorited)" : ""));
        }

        return sb.toString();
    }

}
