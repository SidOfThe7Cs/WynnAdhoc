package sidly.wynnadhoc.utils.build_making;

import net.minecraft.util.Pair;
import sidly.wynnadhoc.wapi.item.IdentificationData;
import sidly.wynnadhoc.wapi.item.WynnItem;
import sidly.wynnadhoc.wapi.item.enums.CraftingStation;
import sidly.wynnadhoc.wapi.item.enums.Identification;
import sidly.wynnadhoc.wapi.item.enums.ItemType;

import java.util.*;

public class Ingredients {

    public enum FlippingResult {
        POSITIVE,
        ABSOLUTE_VALUE,
        NEGATIVE
    }

    public static final Map<CraftingStation, Set<WynnItem>> allIngredientsByType = new HashMap<>();
    public static final Map<String, WynnItem> allIngredients = new HashMap<>();
    public static final Map<CraftingStation, Set<WynnItem>> allNormalIngredientsByType = new HashMap<>();
    public static final Map<String, WynnItem> allNormalIngredients = new HashMap<>();
    public static final Map<CraftingStation, Set<WynnItem>> allMetaIngredientsByType = new HashMap<>();
    public static final Map<String, WynnItem> allMetaIngredients = new HashMap<>();

    public static void updateDatabase(Map<String, WynnItem> itemDatabase) {
        for (Map.Entry<String, WynnItem> entry : itemDatabase.entrySet()) {
            WynnItem item = entry.getValue();
            if (item.type() != ItemType.INGREDIENT) continue;

            boolean isMeta = item.ingredientPositionModifiers() != null && item.ingredientPositionModifiers().hasAny();

            // Update the appropriate global maps
            if (isMeta) {
                allMetaIngredients.put(entry.getKey(), item);
            } else {
                allNormalIngredients.put(entry.getKey(), item);
            }

            if (item.requirements().skills() != null) {
                for (CraftingStation type : item.requirements().skills()) {
                    if (isMeta) {
                        allMetaIngredientsByType.computeIfAbsent(type, k -> new HashSet<>()).add(item);
                    } else {
                        allNormalIngredientsByType.computeIfAbsent(type, k -> new HashSet<>()).add(item);
                    }
                    // Update combined map for all ingredients by type
                    allIngredientsByType.computeIfAbsent(type, k -> new HashSet<>()).add(item);
                }
            } else {
                System.out.println("skills was null for " + entry.getKey());
            }

            // Update combined map for all ingredients
            allIngredients.put(entry.getKey(), item);
        }
    }


    public static WynnItem getIng(String name) {
        return allIngredients.get(name);
    }

    public static List<WynnItem> getIngs(List<Identification> ids, List<Double> valueMultipliers, SkillPoints maxReqs, int extra, CraftingStation type) {
        Map<WynnItem, Double> temp = new HashMap<>();

        Set<WynnItem> options = allNormalIngredientsByType.get(type);
        for (WynnItem ing : options) {
            if (ing.identifications() == null) continue;
            boolean valid = true;
            SkillPoints remaining = SkillPoints.subtract(maxReqs, ing.itemOnlyIDs().getAsSp());
            if (remaining.strength + extra < 0) valid = false;
            if (remaining.dexterity + extra < 0) valid = false;
            if (remaining.intelligence + extra < 0) valid = false;
            if (remaining.defence + extra < 0) valid = false;
            if (remaining.agility + extra < 0) valid = false;

            boolean hasAnId = false;
            double value = 0;
            for (int i = 0; i < ids.size(); i++) {
                Map<Identification, IdentificationData> ingIds = ing.identifications();
                if (ingIds.get(ids.get(i)) != null && ingIds.get(ids.get(i)).getPercent(100) > 0) {
                    hasAnId = true;
                    value += ing.identifications().get(ids.get(i)).getAverage() * valueMultipliers.get(i);
                }
            }
            if (valid && hasAnId) {
                temp.put(ing, value);
            }
        }

        return temp.entrySet().stream()
                .sorted(Map.Entry.<WynnItem, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
    }


    public static Set<WynnItem> getMetaIngs(CraftingStation type) {
        return allMetaIngredientsByType.get(type);
    }

    public static String getMetaIngRecipesSummary(Map<CraftingStation, List<Pair<Recipe, Double>>> options, int... duraPerItem) {
        StringBuilder sb = new StringBuilder();

        for (CraftingStation station : CraftingStation.values()) {
            sb.append("**").append(station).append("**\n");

            List<Pair<Recipe, Double>> combos = options.getOrDefault(station, List.of());

            // Sort descending by total effectiveness
            List<String> sorted = combos.stream()
                    .sorted((p1, p2) -> Double.compare(p2.getRight(), p1.getRight()))
                    .map(p -> getRecipeString(p, duraPerItem))
                    .toList();

            sorted.forEach(sb::append);

            //sb.append("\n**").append(station).append(" (for flipping)**\n");
            //sorted.stream().skip(Math.max(0, sorted.size() - 4)).forEach(sb::append);

            sb.append("\n"); // separate each enum by a blank line
        }

        return sb.toString();
    }

    private static String getRecipeString(Pair<Recipe, Double> pair, int... duraPerItem) {
        int nullIngsDura = 0;
        WynnItem[] ingredients = pair.getLeft().getIngredients();
        int index = 0;
        for (WynnItem ing : ingredients) {
            if (ing != null) continue;
            int durability;
            if (duraPerItem.length == 0) {
                continue;
            } else if (index < duraPerItem.length) {
                durability = duraPerItem[index];
            } else {
                durability = duraPerItem[duraPerItem.length - 1];
            }
            nullIngsDura += durability;
            index++;
        }
        return pair.getLeft().toIngredientString() + ": " + pair.getRight() + " dura: " +
                (pair.getLeft().getRemainingDura() - nullIngsDura)
                + "\n";
    }

    public static Map<CraftingStation, List<Pair<Recipe, Double>>> findBestFlipping(
            CraftableType itemType,
            int limit,
            FlippingResult flippingResult,
            int duraItem,
            int remainingDuraPerSlot,
            int finalDura) {
        Map<CraftingStation, List<Pair<Recipe, Double>>> sortedResults = new HashMap<>();

        List<WynnItem> metaIngredients = new ArrayList<>(allMetaIngredientsByType.get(itemType.getStation()));
        int totalSlots = 6;
        int maxToPlace = 5 - duraItem;

        // Min-heap storing worst at top so we can drop it when size > limit
        PriorityQueue<Pair<Recipe, Double>> bestQueue = new PriorityQueue<>(Comparator.comparingDouble(Pair::getRight));

        for (int numToPlace = 0; numToPlace <= maxToPlace; numToPlace++) {
            List<int[]> positionCombos = generateCombinations(totalSlots, numToPlace, false);
            List<int[]> ingredientChoices = generateCombinations(metaIngredients.size(), numToPlace, true);

            for (int[] positions : positionCombos) {
                for (int[] choice : ingredientChoices) {
                    WynnItem[] grid = new WynnItem[totalSlots];
                    Arrays.fill(grid, null);

                    for (int i = 0; i < numToPlace; i++) {
                        grid[positions[i]] = metaIngredients.get(choice[i]);
                    }

                    // insert dura item (very inefficiently)
                    for (int temp = 0; temp < duraItem; temp++) {
                        Set<WynnItem> normalItems = allNormalIngredientsByType.get(itemType.getStation());
                        WynnItem highestDurabilityItem = normalItems.stream()
                                .max(Comparator.comparingInt(item -> item.itemOnlyIDs().durabilityModifier()))
                                .orElse(null);

                        Recipe tempRecipe = new Recipe(grid, itemType, 3, 118);
                        Double[] tempMultipliers = tempRecipe.getMultipliers();
                        for (int i = 0; i < tempMultipliers.length; i++) {
                            switch (flippingResult) {
                                case POSITIVE -> {
                                }
                                case NEGATIVE -> tempMultipliers[i] *= -1;
                                case ABSOLUTE_VALUE -> tempMultipliers[i] = Math.abs(tempMultipliers[i]);
                            }
                        }

                        // Find the null slot with the lowest multiplier
                        int lowestIndex = -1;
                        double lowestValue = Double.MAX_VALUE;
                        for (int i = 0; i < grid.length; i++) {
                            if (grid[i] == null && tempMultipliers[i] < lowestValue) {
                                lowestValue = tempMultipliers[i];
                                lowestIndex = i;
                            }
                        }

                        // Insert the highestDurabilityItem into that slot
                        if (lowestIndex != -1) {
                            grid[lowestIndex] = highestDurabilityItem;
                        }
                    }

                    Recipe recipe = new Recipe(grid, itemType, 3, 118);
                    int emptySlots = (int) Arrays.stream(grid).filter(Objects::isNull).count();
                    int duraRemaining = recipe.getRemainingDura() - finalDura;
                    int duraPerSlot = duraRemaining / emptySlots;
                    if (duraPerSlot < remainingDuraPerSlot) continue;


                    Double[] multipliers = recipe.getMultipliers();
                    WynnItem[] ings = recipe.getIngredients();

                    // Collect multipliers of empty slots
                    List<Double> emptyMultipliers = new ArrayList<>();
                    for (int i = 0; i < ings.length; i++) {
                        if (ings[i] == null && multipliers[i] != null) {
                            emptyMultipliers.add(multipliers[i]);
                        }
                    }
                    if (emptyMultipliers.isEmpty()) continue;

                    // Transform multipliers based on flippingResult
                    List<Double> transformed = new ArrayList<>(emptyMultipliers.size());
                    for (double val : emptyMultipliers) {
                        switch (flippingResult) {
                            case POSITIVE -> transformed.add(val);
                            case NEGATIVE -> transformed.add(-val);
                            case ABSOLUTE_VALUE -> transformed.add(Math.abs(val));
                        }
                    }

                    // Compute adjusted sum
                    double adjustedSum = transformed.stream()
                            .mapToDouble(Double::doubleValue)
                            .sum();


                    // Maintain only top 'limit'
                    if (bestQueue.size() < limit) {
                        bestQueue.offer(new Pair<>(recipe, adjustedSum));
                    } else if (adjustedSum > bestQueue.peek().getRight()) {
                        bestQueue.poll();
                        bestQueue.offer(new Pair<>(recipe, adjustedSum));
                    }
                }
            }
        }

        // Convert best recipes into MetaPlacementResults
        List<Pair<Recipe, Double>> topResults = bestQueue.stream()
                .sorted((a, b) -> Double.compare(b.getRight(), a.getRight()))
                .toList();

        sortedResults.put(itemType.getStation(), topResults);

        return sortedResults;
    }


    public static List<int[]> generateCombinations(int n, int k, boolean allowDuplicates) {
        List<int[]> combinations = new ArrayList<>();
        if (k < 0) return combinations;
        if (!allowDuplicates && k > n) return combinations; // can't pick more unique elements than exist

        if (k == 0) {
            combinations.add(new int[0]);
            return combinations;
        }

        if (allowDuplicates) {
            generateCombinationsWithRepetitionRecursive(new int[k], 0, n, combinations);
        } else {
            generateCombinationsRecursive(0, n, k, new int[k], 0, combinations);
        }

        return combinations;
    }

    // unique only
    private static void generateCombinationsRecursive(int startIndex, int n, int k, int[] currentCombination, int depth, List<int[]> out) {
        if (depth == k) {
            out.add(Arrays.copyOf(currentCombination, k));
            return;
        }

        // prune: last start is n - (k - depth)
        for (int i = startIndex; i <= n - (k - depth); i++) {
            currentCombination[depth] = i;
            generateCombinationsRecursive(i + 1, n, k, currentCombination, depth + 1, out);
        }
    }

    // allow duplicates
    private static void generateCombinationsWithRepetitionRecursive(int[] current, int depth, int n, List<int[]> out) {
        if (depth == current.length) {
            out.add(Arrays.copyOf(current, current.length));
            return;
        }

        for (int i = 0; i < n; i++) {
            current[depth] = i;
            generateCombinationsWithRepetitionRecursive(current, depth + 1, n, out);
        }
    }

}
