package sidly.wynnadhoc.utils.build_making;

import sidly.wynnadhoc.wapi.ApiUtils;

public class Tester {
    public static void main() {
        RecipeLoader.loadRecipes();
        Ingredients.updateDatabase(ApiUtils.getItemDatabase());
        System.out.println(Ingredients.getMetaIngRecipesSummary(Ingredients.findBestFlipping(
                CraftableType.LEGGINGS,
                20,
                Ingredients.FlippingResult.POSITIVE,
                1,
                183,
                100
        ), 183));
    }
}
