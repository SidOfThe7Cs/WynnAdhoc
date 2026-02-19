package sidly.wynnadhoc.features.outervoid;

import com.wynntils.models.gear.type.GearTier;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.ItemEntity;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OuterVoidItemDatabase {
    public static class Pair{
        public String name;
        public float model;

        Pair(String name, float model){
            this.name = name;
            this.model = model;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Pair pair2){
                return Objects.equals(pair2.name, this.name) && Float.compare(pair2.model, this.model) == 0;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, model);
        }

    }

    // Predefined dataset (can be expanded as needed)
    private static final Map<Pair, GearTier> itemRarityMap = new HashMap<>();

    static public void init() {
        Pair Amphora_Sherd = new Pair("Brick", -1.0F);
        itemRarityMap.put(Amphora_Sherd, GearTier.UNIQUE);

        Pair Bleached_Branch = new Pair("Stick", -1.0F);
        itemRarityMap.put(Bleached_Branch, GearTier.UNIQUE);

        Pair Discarded_Scrap = new Pair("Paper", 18.0F);
        itemRarityMap.put(Discarded_Scrap, GearTier.UNIQUE);

        Pair Fossilized_Starfish = new Pair("Paper", 85.0F);
        itemRarityMap.put(Fossilized_Starfish, GearTier.UNIQUE);

        Pair Fallen_Blade = new Pair("Flint", -1.0F);
        itemRarityMap.put(Fallen_Blade, GearTier.UNIQUE);

        Pair Fallen_Sand = new Pair("Red Sand", -1.0F);
        itemRarityMap.put(Fallen_Sand, GearTier.UNIQUE);

        Pair Lightless_Blossom = new Pair("Pink Tulip", -1.0F);
        itemRarityMap.put(Lightless_Blossom, GearTier.UNIQUE);

        Pair Matte_Bead = new Pair("Clay Ball", -1.0F);
        itemRarityMap.put(Matte_Bead, GearTier.UNIQUE);

        Pair Metal_Swarf = new Pair("Iron Nugget", -1.0F);
        itemRarityMap.put(Metal_Swarf, GearTier.UNIQUE);

        Pair Mosaic_Tile = new Pair("White Glazed Terracotta", -1.0F);
        itemRarityMap.put(Mosaic_Tile, GearTier.UNIQUE);

        Pair Shriveled_Voidgloom = new Pair("Chorus Plant", -1.0F);
        itemRarityMap.put(Shriveled_Voidgloom, GearTier.UNIQUE);

        Pair Void_Slime = new Pair("Slimeball", -1.0F);
        itemRarityMap.put(Void_Slime, GearTier.UNIQUE);

        Pair Voidwarped_Root = new Pair("Chorus Fruit", -1.0F);
        itemRarityMap.put(Voidwarped_Root, GearTier.UNIQUE);

        Pair Wind_shorn_Stone = new Pair("Stone Button", -1.0F);
        itemRarityMap.put(Wind_shorn_Stone, GearTier.UNIQUE);

        Pair Abandoned_Pot = new Pair("Flower Pot", -1.0F);
        itemRarityMap.put(Abandoned_Pot, GearTier.RARE);

        Pair Almanac_Page = new Pair("Paper", -1.0F);
        itemRarityMap.put(Almanac_Page, GearTier.RARE);

        Pair Arable_Chunk = new Pair("Grass Block", -1.0F);
        itemRarityMap.put(Arable_Chunk, GearTier.RARE);

        Pair Bottle_of_Yogurt = new Pair("Glass Bottle", -1.0F);
        itemRarityMap.put(Bottle_of_Yogurt, GearTier.RARE);

        Pair Due_Delivery = new Pair("Piston", -1.0F);
        itemRarityMap.put(Due_Delivery, GearTier.RARE);

        Pair Elestial_Voidstone = new Pair("Paper", 73.0F);
        itemRarityMap.put(Elestial_Voidstone, GearTier.RARE);

        Pair Frying_Pan = new Pair("Diamond Shovel", -1.0F);
        itemRarityMap.put(Frying_Pan, GearTier.RARE);

        Pair Hobby_Horse = new Pair("Iron Horse Armor", 542.0F);
        itemRarityMap.put(Hobby_Horse, GearTier.RARE);

        Pair Lone_Component = new Pair("Brewing Stand", -1.0F);
        itemRarityMap.put(Lone_Component, GearTier.RARE);

        Pair Metal_Plate = new Pair("Iron Trapdoor", -1.0F);
        itemRarityMap.put(Metal_Plate, GearTier.RARE);

        Pair Missing_Coinage = new Pair("Paper", 68.0F);
        itemRarityMap.put(Missing_Coinage, GearTier.RARE);

        Pair Packaged_Brownie = new Pair("Nether Brick", -1.0F);
        itemRarityMap.put(Packaged_Brownie, GearTier.RARE);

        Pair Steel_Rod = new Pair("End Rod", -1.0F);
        itemRarityMap.put(Steel_Rod, GearTier.RARE);

        Pair Void_Carapace = new Pair("Shulker Shell", -1.0F);
        itemRarityMap.put(Void_Carapace, GearTier.RARE);

        Pair Black_Prism = new Pair("Black Stained Glass", -1.0F);
        itemRarityMap.put(Black_Prism, GearTier.LEGENDARY);

        Pair History_Textbook = new Pair("Daylight Detector", -1.0F);
        itemRarityMap.put(History_Textbook, GearTier.LEGENDARY);

        Pair Large_Metal_Chunk = new Pair("Block of Iron", -1.0F);
        itemRarityMap.put(Large_Metal_Chunk, GearTier.LEGENDARY);

        Pair Luxury_Timepiece = new Pair("Clock", -1.0F);
        itemRarityMap.put(Luxury_Timepiece, GearTier.LEGENDARY);

        Pair Miracle_Leftovers = new Pair("Mushroom Stew", -1.0F);
        itemRarityMap.put(Miracle_Leftovers, GearTier.LEGENDARY);

        Pair Precious_Mineral = new Pair("Paper", 59.0F);
        itemRarityMap.put(Precious_Mineral, GearTier.LEGENDARY);

        Pair Small_Ruby = new Pair("Paper", 72.0F);
        itemRarityMap.put(Small_Ruby, GearTier.LEGENDARY);

        Pair Golem_Capacitor = new Pair("Beacon", -1.0F);
        itemRarityMap.put(Golem_Capacitor, GearTier.FABLED);

        Pair Relic_of_the_Shattering = new Pair("Totem of Undying", -1.0F);
        itemRarityMap.put(Relic_of_the_Shattering, GearTier.FABLED);

        Pair Tangible_Intangibility = new Pair("Purple Glazed Terracotta", -1.0F);
        itemRarityMap.put(Tangible_Intangibility, GearTier.FABLED);

        Pair Archaic_Writing = new Pair("Snow", -1.0F); // very not sure about this
        itemRarityMap.put(Archaic_Writing, GearTier.MYTHIC);


        //why are they differnet when they spawn in that when i pick them up and drop them :cry::cry:
        // diamond axes with durabilty instead of custom model data
        Pair Discarded_Scrap2 = new Pair("Diamond Axe", 96.0F);
        itemRarityMap.put(Discarded_Scrap2, GearTier.UNIQUE);

        Pair Fossilized_Starfish2 = new Pair("Diamond Axe", 28.0F);
        itemRarityMap.put(Fossilized_Starfish2, GearTier.UNIQUE);

        Pair Elestial_Voidstone2 = new Pair("Diamond Axe", 66.0F);
        itemRarityMap.put(Elestial_Voidstone2, GearTier.RARE);

        Pair Missing_Coinage2 = new Pair("Diamond Axe", 61.0F);
        itemRarityMap.put(Missing_Coinage2, GearTier.RARE);

        Pair Small_Ruby2 = new Pair("Diamond Axe", 65.0F);
        itemRarityMap.put(Small_Ruby2, GearTier.LEGENDARY);

        Pair Precious_Mineral2 = new Pair("Diamond Axe", 53.0F);
        itemRarityMap.put(Precious_Mineral2, GearTier.LEGENDARY);
    }

    // Function to get rarity based on vanilla name and optional custom_model_data
    public static GearTier getRarity(String name, float model) {
        Pair item = new Pair(name, model);
        if (itemRarityMap.containsKey(item)) {
            return itemRarityMap.get(item);
        }
        return GearTier.CRAFTED;
    }
    public static GearTier getRarity(ItemEntity item) {
        String name = item.getStack().getName().getString();
        CustomModelDataComponent modelData = item.getStack().get(DataComponentTypes.CUSTOM_MODEL_DATA);
        float ID;
        if (modelData != null) {
            ID = modelData.floats().getFirst();
        } else ID = -1;
        if (Objects.equals(name, "Diamond Axe")) {
            ID = item.getStack().getDamage();
        }
        return getRarity(name, ID);
    }

    public static Color getColor(GearTier rarity) {
        return switch (rarity) {
            case NORMAL -> Color.WHITE;
            case UNIQUE -> new Color(0xF2F250);
            case RARE -> new Color(0xF652F7);
            case SET -> new Color(0x89EF57);
            case LEGENDARY -> new Color(0x75DDE1);
            case FABLED -> Color.RED;
            case MYTHIC -> new Color(0xA116A1);
            default -> new Color(0x00710e);
        };
    }
}
