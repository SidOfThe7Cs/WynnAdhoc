package sidly.wynnadhoc.features.chests;

import com.wynntils.models.gear.type.GearTier;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.saves.ChestsSaveData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ChestDataCache(
        Map<GearTier, Integer> globalItemCounts,
        Map<GearTier, Integer> localItemCounts,
        Map<Integer, Integer> globalIngCounts,
        Map<Integer, Integer> localIngCounts,
        Map<Integer, Integer> globalItemPercents,
        Map<Integer, Integer> localItemPercents,
        Map<Integer, Integer> globalIngPercents,
        Map<Integer, Integer> localIngPercents
) {

    public void addLocalCounts(byte[] newItems) {
        List<EncodableItem> localItems = EncodableItem.fromByteArray(newItems);

        Map<GearTier, Integer> itemCounts = new HashMap<>();
        Map<Integer, Integer> ingCounts = new HashMap<>();

        for (EncodableItem item : localItems) {
            if (item instanceof BoxItem(GearTier rarity, int minLvl)) {
                itemCounts.merge(rarity, 1, Integer::sum);
            } else if (item instanceof IngredientItem(int tier, int lvl)) {
                ingCounts.merge(tier, 1, Integer::sum);
            } else WynnAdhocClient.LOGGER.warn("unknown item type when calculating local counts: " + item);
        }

        itemCounts.forEach((key, value) -> this.localItemCounts.merge(key, value, Integer::sum));
        ingCounts.forEach((key, value) -> this.localIngCounts.merge(key, value, Integer::sum));
    }

    public void updateLocal(byte[] totalItemBytes) {
        Map<GearTier, Integer> localItemCounts = new HashMap<>();
        Map<Integer, Integer> localIngCounts = new HashMap<>();
        Map<Integer, Integer> localItemPercents = new HashMap<>();
        Map<Integer, Integer> localIngPercents = new HashMap<>();

        List<EncodableItem> localItems = EncodableItem.fromByteArray(totalItemBytes);
        calculateData(localItems, localItemCounts, localIngCounts, localItemPercents, localIngPercents);

        this.localItemPercents.clear();
        this.localItemPercents.putAll(localItemPercents);

        this.localItemCounts.clear();
        this.localItemCounts.putAll(localItemCounts);

        this.localIngPercents.clear();
        this.localIngPercents.putAll(localIngPercents);

        this.localIngCounts.clear();
        this.localIngCounts.putAll(localIngCounts);
    }

    public static ChestDataCache from(ChestsSaveData.ChestData local, LootChest global) {
        Map<GearTier, Integer> globalItemCounts = new HashMap<>();
        Map<GearTier, Integer> localItemCounts = new HashMap<>();
        Map<Integer, Integer> globalIngCounts = new HashMap<>();
        Map<Integer, Integer> localIngCounts = new HashMap<>();
        Map<Integer, Integer> globalItemPercents = new HashMap<>();
        Map<Integer, Integer> localItemPercents = new HashMap<>();
        Map<Integer, Integer> globalIngPercents = new HashMap<>();
        Map<Integer, Integer> localIngPercents = new HashMap<>();

        if (global != null) {
            List<EncodableItem> globalItems = EncodableItem.fromByteArray(global.chestData());
            calculateData(globalItems, globalItemCounts, globalIngCounts, globalItemPercents, globalIngPercents);
        }

        if (local != null) {
            List<EncodableItem> localItems = EncodableItem.fromByteArray(local.items);
            calculateData(localItems, localItemCounts, localIngCounts, localItemPercents, localIngPercents);
        }

        return new ChestDataCache(globalItemCounts, localItemCounts, globalIngCounts, localIngCounts, globalItemPercents, localItemPercents, globalIngPercents, localIngPercents);
    }

    private static void calculateData(List<EncodableItem> items, Map<GearTier, Integer> itemCounts, Map<Integer, Integer> ingCounts, Map<Integer, Integer> itemPercents, Map<Integer, Integer> ingPercents) {
        int totalItemCount = 0;
        int totalIngCount = 0;
        Map<Integer, Integer> itemLvlCounts = new HashMap<>();
        Map<Integer, Integer> ingLvlCounts = new HashMap<>();

        for (EncodableItem item : items) {
            if (item instanceof BoxItem(GearTier rarity, int minLvl)) {
                itemCounts.merge(rarity, 1, Integer::sum);
                totalItemCount++;
                itemLvlCounts.merge(minLvl, 1, Integer::sum);
            } else if (item instanceof IngredientItem(int tier, int lvl)) {
                ingCounts.merge(tier, 1, Integer::sum);
                totalIngCount++;
                ingLvlCounts.merge(lvl, 1, Integer::sum);
            } else WynnAdhocClient.LOGGER.warn("unknown item type when calculating data: " + item);
        }
        final int finalItemCount = totalItemCount;
        final int finalIngCount = totalIngCount;

        if (finalItemCount != 0) {
            itemLvlCounts.forEach((key, value) -> {
                int percent = (int) ((value / (double) finalItemCount) * 100);
                itemPercents.put(key, percent);
            });
        }

        if (finalIngCount != 0) {
            ingLvlCounts.forEach((key, value) -> {
                int percent = (int) ((value / (double) finalIngCount) * 100);
                ingPercents.put(key, percent);
            });
        }
    }
}
