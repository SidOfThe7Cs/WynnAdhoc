package sidly.wynnadhoc.utils.build_making;


import sidly.wynnadhoc.utils.FormatUtils;
import sidly.wynnadhoc.wapi.item.WynnItem;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class Recipe {
    private final WynnItem[] ingredients;
    private final CraftableType type;
    private final int materialTier;
    private final int level;
    private final Double[] multipliers;

    public Recipe(String[] ingredients, CraftableType itemType, int materialTier, int level) {
        this(
                Arrays.stream(ingredients)
                        .map(Ingredients::getIng)
                        .toArray(WynnItem[]::new),
                itemType,
                materialTier,
                level
        );
    }

    public Recipe(WynnItem[] ingredients, CraftableType itemType, int materialTier, int level) {
        if (ingredients.length != 6) {
            throw new IllegalArgumentException("A recipe must have exactly 6 ingredients (2x3).");
        }

        this.ingredients = ingredients;
        this.type = itemType;
        this.materialTier = materialTier;
        this.level = Math.clamp(level, 1, 103);
        this.multipliers = initMultipliers();
    }


    public WynnItem[] getIngredients() {
        return ingredients;
    }

    public CraftableType getType() {
        return type;
    }

    public int getMaterialTier() {
        return materialTier;
    }

    public int getLevel() {
        return level;
    }

    public Double[] getMultipliers() {
        return multipliers;
    }

    public Double[] initMultipliers() {
        Double[] multipliers = new Double[6];
        Arrays.fill(multipliers, 1.0);

        for (int slot = 0; slot < ingredients.length; slot++) {
            if (ingredients[slot] == null) continue;
            Double[] modArray = ingredients[slot].ingredientPositionModifiers().getMultipliers(slot);
            for (int i = 0; i < multipliers.length; i++) {
                multipliers[i] += modArray[i];
            }
        }

        return multipliers;
    }

    public double computeTotalMultiplierForEmptySlots() {
        double total = 0;
        Double[] multipliers = getMultipliers();

        for (int i = 0; i < multipliers.length; i++) {
            if (this.ingredients[i] == null) total += multipliers[i];
        }

        return FormatUtils.ignoreFloatingPointErrors(total);
    }

    @Override
    public String toString() {
        return type.getStation() + " " + Arrays.toString(ingredients);
    }

    public String toIngredientString() {
        return Arrays.stream(ingredients)
                .map(item -> item == null ? "null" : item.internalName())
                .collect(Collectors.joining(", "));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Recipe other) {
            return Arrays.equals(this.ingredients, other.ingredients);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(ingredients);
    }

    public int getRemainingDura() {
        Optional<RecipeLoader.RecipeData> recipe = RecipeLoader.getRecipe(type, level);
        if (recipe.isEmpty()) return -1;
        int dura = 805; // TODO fix recipe loading and material multipluiers
        for (WynnItem ing : ingredients) {
            if (ing == null) continue;
            dura += ing.itemOnlyIDs().durabilityModifier() / 1000;
        }
        return dura;
    }
}
