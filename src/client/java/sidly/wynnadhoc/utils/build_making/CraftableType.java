package sidly.wynnadhoc.utils.build_making;

import sidly.wynnadhoc.wapi.item.enums.CraftingStation;

public enum CraftableType {
    HELMET(CraftingStation.ARMOURING),
    CHESTPLATE(CraftingStation.ARMOURING),
    LEGGINGS(CraftingStation.TAILORING),
    BOOTS(CraftingStation.TAILORING),
    SPEAR(CraftingStation.WEAPONSMITHING),
    DAGGER(CraftingStation.WEAPONSMITHING),
    BOW(CraftingStation.WOODWORKING),
    WAND(CraftingStation.WOODWORKING),
    RELIK(CraftingStation.WOODWORKING),
    RING(CraftingStation.JEWELING),
    BRACELET(CraftingStation.JEWELING),
    NECKLACE(CraftingStation.JEWELING),
    POTION(CraftingStation.ALCHEMISM),
    SCROLL(CraftingStation.SCRIBING),
    FOOD(CraftingStation.COOKING);

    private final CraftingStation station;

    CraftableType(CraftingStation station) {
        this.station = station;
    }

    public CraftingStation getStation() {
        return this.station;
    }

}
