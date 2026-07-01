package sidly.wynnadhoc.features.chests;

import com.wynntils.models.gear.type.GearTier;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.saves.ChestsSaveData;

import java.util.*;

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

    public void updateLocalWith(List<EncodableItem> items) {
        Map<GearTier, Integer> localItemCounts = new HashMap<>();
        Map<Integer, Integer> localIngCounts = new HashMap<>();
        Map<Integer, Integer> localItemPercents = new HashMap<>();
        Map<Integer, Integer> localIngPercents = new HashMap<>();

        calculateData(items, localItemCounts, localIngCounts, localItemPercents, localIngPercents);

        int oldItemCount = this.localItemCounts.values().stream().reduce(Integer::sum).orElse(0);
        int newItemCount = localItemCounts.values().stream().reduce(Integer::sum).orElse(0);
        int itemTotal = oldItemCount + newItemCount;
        int oldIngCount = this.localIngCounts.values().stream().reduce(Integer::sum).orElse(0);
        int newIngCount = localIngCounts.values().stream().reduce(Integer::sum).orElse(0);
        int ingTotal = oldIngCount + newIngCount;

        localItemCounts.forEach((key, value) -> this.localItemCounts.merge(key, value, Integer::sum));
        localIngCounts.forEach((key, value) -> this.localIngCounts.merge(key, value, Integer::sum));

        Map<Integer, Integer> weightedItemPercents = new HashMap<>();
        Set<Integer> allItemKeys = new HashSet<>(this.localItemPercents.keySet());
        allItemKeys.addAll(localItemPercents.keySet());
        for (int key : allItemKeys) {
            int oldPercent = this.localItemPercents.getOrDefault(key, 0);
            int newPercent = localItemPercents.getOrDefault(key, 0);

            int oldCount = (int) ((oldPercent / 100.0) * oldItemCount);
            int newCount = (int) ((newPercent / 100.0) * newItemCount);

            int weightedPercent = itemTotal > 0 ? (int) (((oldCount + newCount) / (double) itemTotal) * 100) : 0;
            weightedItemPercents.put(key, weightedPercent);
        }
        this.localItemPercents.clear();
        this.localItemPercents.putAll(weightedItemPercents);

        Map<Integer, Integer> weightedIngPercents = new HashMap<>();
        Set<Integer> allIngKeys = new HashSet<>(this.localIngPercents.keySet());
        allIngKeys.addAll(localIngPercents.keySet());
        for (int key : allIngKeys) {
            int oldPercent = this.localIngPercents.getOrDefault(key, 0);
            int newPercent = localIngPercents.getOrDefault(key, 0);

            int oldCount = (int) ((oldPercent / 100.0) * oldIngCount);
            int newCount = (int) ((newPercent / 100.0) * newIngCount);

            int weightedPercent = ingTotal > 0 ? (int) (((oldCount + newCount) / (double) ingTotal) * 100) : 0;
            weightedIngPercents.put(key, weightedPercent);
        }
        this.localIngPercents.clear();
        this.localIngPercents.putAll(weightedIngPercents);
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
