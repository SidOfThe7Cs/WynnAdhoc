package sidly.wynnadhoc.wapi.item;

import sidly.wynnadhoc.wapi.item.enums.CraftingStation;
import sidly.wynnadhoc.wapi.item.enums.Quest;
import sidly.wynnadhoc.wapi.item.enums.WynnClass;

import java.util.List;

public record Requirements(int level, int strength, int dexterity, int intelligence, int defence, int agility,
                           Quest quest, WynnClass classRequirement, List<CraftingStation> skills) {
}
