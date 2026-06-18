package sidly.wynnadhoc.wapi.item;

import sidly.wynnadhoc.wapi.item.enums.*;

import java.util.List;
import java.util.Map;

public record WynnItem(
        String internalName,
        ItemType type,
        SubType subType,
        Icon icon,
        Emblem emblem,
        Tier tier,
        AttackSpeed attackSpeed,
        Restriction restriction,
        DropRestriction dropRestriction,
        List<Element> elements,
        Requirements requirements,
        String lore,
        Map<Identification, IdentificationData> identifications,
        Map<Base, IdentificationData> base,
        boolean allowCraftsman,
        double averageDps,
        Chances chances,
        ConsumableOnlyIDs consumableOnlyIDs,
        DropMeta dropMeta,
        List<DroppedBy> droppedBy,
        int durability,
        int gatheringSpeed,
        boolean identified,
        IngredientPositionModifiers ingredientPositionModifiers,
        ItemOnlyIDs itemOnlyIDs,
        Map<MajorID, String> majorIds,
        int powderSlots,
        List<String> sets
) {

}