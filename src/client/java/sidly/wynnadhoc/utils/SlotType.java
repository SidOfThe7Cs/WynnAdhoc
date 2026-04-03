package sidly.wynnadhoc.utils;

import java.util.HashMap;
import java.util.Map;

public enum SlotType {
    NEXT_PAGE,
    LAST_PAGE;

    public static Map<String, Map<Integer, SlotType>> slotTypeMap = new HashMap<>();

    public static void addSlotType(String title, Integer slot, SlotType slotType) {
        Map<Integer, SlotType> map = slotTypeMap.computeIfAbsent(title, k -> new HashMap<>());
        map.put(slot, slotType);
    }
}
