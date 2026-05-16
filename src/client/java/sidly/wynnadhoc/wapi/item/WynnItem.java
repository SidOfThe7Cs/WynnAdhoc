package sidly.wynnadhoc.wapi.item;

import org.jspecify.annotations.NonNull;
import sidly.wynnadhoc.wapi.item.enums.*;

import java.util.List;
import java.util.Map;

// TODO add name
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
    @Override
    public @NonNull String toString() {
        return "WynnItem {\n" +
                "  internalName = " + internalName + "\n" +
                "  type = " + type + "\n" +
                "  subType = " + subType + "\n" +
                "  icon = " + icon + "\n" +
                "  emblem = " + emblem + "\n" +
                "  tier = " + tier + "\n" +
                "  attackSpeed = " + attackSpeed + "\n" +
                "  restriction = " + restriction + "\n" +
                "  dropRestriction = " + dropRestriction + "\n" +
                "  elements = " + elements + "\n" +
                "  requirements = " + requirements + "\n" +
                "  lore = " + (lore != null ? "\"" + lore.replace("\n", "\\n") + "\"" : null) + "\n" +
                "  identifications = " + identifications + "\n" +
                "  base = " + base + "\n" +
                "  allowCraftsman = " + allowCraftsman + "\n" +
                "  averageDps = " + averageDps + "\n" +
                "  chances = " + chances + "\n" +
                "  consumableOnlyIDs = " + consumableOnlyIDs + "\n" +
                "  dropMeta = " + dropMeta + "\n" +
                "  droppedBy = " + droppedBy + "\n" +
                "  durability = " + durability + "\n" +
                "  gatheringSpeed = " + gatheringSpeed + "\n" +
                "  identified = " + identified + "\n" +
                "  ingredientPositionModifiers = " + ingredientPositionModifiers + "\n" +
                "  itemOnlyIDs = " + itemOnlyIDs + "\n" +
                "  majorIds = " + majorIds + "\n" +
                "  powderSlots = " + powderSlots + "\n" +
                "  sets = " + sets + "\n" +
                "}";
    }

    // TODO
    public String idsToString(Map<Enum<?>, IdentificationData> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Enum<?>, IdentificationData> entry : map.entrySet()) {
            sb.append("   ");
            sb.append(entry.getKey().name());
            sb.append(": ");
            sb.append(entry.getValue().toString());
            sb.append("\n");

        }
        return sb.toString();
    }

    public static class Builder {
        private String internalName;
        private ItemType type;
        private SubType subType;
        private Icon icon;
        private Emblem emblem;
        private Tier tier;
        private AttackSpeed attackSpeed;
        private Restriction restriction;
        private DropRestriction dropRestriction;
        private List<Element> elements;
        private Requirements requirements;
        private String lore;
        private Map<Identification, IdentificationData> identifications;
        private Map<Base, IdentificationData> base;
        private boolean allowCraftsman;
        private double averageDps;
        private Chances chances;
        private ConsumableOnlyIDs consumableOnlyIDs;
        private DropMeta dropMeta;
        private List<DroppedBy> droppedBy;
        private int durability;
        private int gatheringSpeed;
        private boolean identified;
        private IngredientPositionModifiers ingredientPositionModifiers;
        private ItemOnlyIDs itemOnlyIDs;
        private Map<MajorID, String> majorIds;
        private int powderSlots;
        private List<String> sets;

        public Builder() {
        }

        public Builder internalName(String internalName) {
            this.internalName = internalName;
            return this;
        }

        public Builder type(ItemType type) {
            this.type = type;
            return this;
        }

        public Builder subType(SubType subType) {
            this.subType = subType;
            return this;
        }

        public Builder icon(Icon icon) {
            this.icon = icon;
            return this;
        }

        public Builder emblem(Emblem emblem) {
            this.emblem = emblem;
            return this;
        }

        public Builder tier(Tier tier) {
            this.tier = tier;
            return this;
        }

        public Builder attackSpeed(AttackSpeed attackSpeed) {
            this.attackSpeed = attackSpeed;
            return this;
        }

        public Builder restriction(Restriction restriction) {
            this.restriction = restriction;
            return this;
        }

        public Builder dropRestriction(DropRestriction dropRestriction) {
            this.dropRestriction = dropRestriction;
            return this;
        }

        public Builder elements(List<Element> elements) {
            this.elements = elements;
            return this;
        }

        public Builder requirements(Requirements requirements) {
            this.requirements = requirements;
            return this;
        }

        public Builder lore(String lore) {
            this.lore = lore;
            return this;
        }

        public Builder identifications(Map<Identification, IdentificationData> identifications) {
            this.identifications = identifications;
            return this;
        }

        public Builder base(Map<Base, IdentificationData> base) {
            this.base = base;
            return this;
        }

        public Builder allowCraftsman(boolean allowCraftsman) {
            this.allowCraftsman = allowCraftsman;
            return this;
        }

        public Builder averageDps(double averageDps) {
            this.averageDps = averageDps;
            return this;
        }

        public Builder chances(Chances chances) {
            this.chances = chances;
            return this;
        }

        public Builder consumableOnlyIDs(ConsumableOnlyIDs consumableOnlyIDs) {
            this.consumableOnlyIDs = consumableOnlyIDs;
            return this;
        }

        public Builder dropMeta(DropMeta dropMeta) {
            this.dropMeta = dropMeta;
            return this;
        }

        public Builder droppedBy(List<DroppedBy> droppedBy) {
            this.droppedBy = droppedBy;
            return this;
        }

        public Builder durability(int durability) {
            this.durability = durability;
            return this;
        }

        public Builder gatheringSpeed(int gatheringSpeed) {
            this.gatheringSpeed = gatheringSpeed;
            return this;
        }

        public Builder identified(boolean identified) {
            this.identified = identified;
            return this;
        }

        public Builder ingredientPositionModifiers(IngredientPositionModifiers ingredientPositionModifiers) {
            this.ingredientPositionModifiers = ingredientPositionModifiers;
            return this;
        }

        public Builder itemOnlyIDs(ItemOnlyIDs itemOnlyIDs) {
            this.itemOnlyIDs = itemOnlyIDs;
            return this;
        }

        public Builder majorIds(Map<MajorID, String> majorIds) {
            this.majorIds = majorIds;
            return this;
        }

        public Builder powderSlots(int powderSlots) {
            this.powderSlots = powderSlots;
            return this;
        }

        public Builder sets(List<String> sets) {
            this.sets = sets;
            return this;
        }

        public WynnItem build() {
            return new WynnItem(
                    internalName,
                    type,
                    subType,
                    icon,
                    emblem,
                    tier,
                    attackSpeed,
                    restriction,
                    dropRestriction,
                    elements,
                    requirements,
                    lore,
                    identifications,
                    base,
                    allowCraftsman,
                    averageDps,
                    chances,
                    consumableOnlyIDs,
                    dropMeta,
                    droppedBy,
                    durability,
                    gatheringSpeed,
                    identified,
                    ingredientPositionModifiers,
                    itemOnlyIDs,
                    majorIds,
                    powderSlots,
                    sets
            );
        }
    }
}