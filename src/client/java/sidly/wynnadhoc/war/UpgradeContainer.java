package sidly.wynnadhoc.war;

import java.util.HashMap;
import java.util.Map;

public class UpgradeContainer {

    private final Map<String, Upgrade> byName = new HashMap<>();
    private final Map<Integer, Upgrade> bySlot = new HashMap<>();

    public void add(String name, int slotIndex, ResourceType type) {
        Upgrade upgrade = new Upgrade(name, slotIndex, type);

        byName.put(upgrade.getName(), upgrade);
        bySlot.put(upgrade.getSlotIndex(), upgrade);
    }

    public Upgrade getByName(String name) {
        return byName.get(name);
    }

    public Map<String, Upgrade> getAllByName() {
        return this.byName;
    }

    public Upgrade getBySlot(int slotIndex) {
        return bySlot.get(slotIndex);
    }

    public boolean containsName(String name) {
        return byName.containsKey(name);
    }

    public boolean containsSlot(int slotIndex) {
        return bySlot.containsKey(slotIndex);
    }


    public int getEmeraldsPerHourConsume() {
        int result = 0;
        for (Map.Entry<Integer, Upgrade> entry : bySlot.entrySet()){
            if (entry.getValue().getResourceType().equals(ResourceType.Emeralds)){
                result += entry.getValue().getCost();
            }
        }
        return result;
    }

    public int getOrePerHourConsume() {
        int result = 0;
        for (Map.Entry<Integer, Upgrade> entry : bySlot.entrySet()){
            if (entry.getValue().getResourceType().equals(ResourceType.Ore)){
                result += entry.getValue().getCost();
            }
        }
        return result;
    }

    public int getCropsPerHourConsume() {
        int result = 0;
        for (Map.Entry<Integer, Upgrade> entry : bySlot.entrySet()){
            if (entry.getValue().getResourceType().equals(ResourceType.Crops)){
                result += entry.getValue().getCost();
            }
        }
        return result;
    }

    public int getFishPerHourConsume() {
        int result = 0;
        for (Map.Entry<Integer, Upgrade> entry : bySlot.entrySet()){
            if (entry.getValue().getResourceType().equals(ResourceType.Fish)){
                result += entry.getValue().getCost();
            }
        }
        return result;
    }

    public int getWoodPerHourConsume() {
        int result = 0;
        for (Map.Entry<Integer, Upgrade> entry : bySlot.entrySet()){
            if (entry.getValue().getResourceType().equals(ResourceType.Wood)){
                result += entry.getValue().getCost();
            }
        }
        return result;
    }

    public void clear() {
        for (Upgrade upgrade : bySlot.values()) {
            upgrade.setStackSize(0);
        }
        for (Upgrade upgrade : byName.values()) {
            upgrade.setStackSize(0);
        }
    }
}

