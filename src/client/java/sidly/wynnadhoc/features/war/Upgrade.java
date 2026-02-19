package sidly.wynnadhoc.features.war;

import java.util.HashMap;
import java.util.Map;

public class Upgrade {

    private int stackSize = 0;
    private final String name;
    private final int slotIndex;
    private final Map<Integer, Level> levels = new HashMap<>();
    private final ResourceType resourceType;

    public Upgrade(String name, int slotIndex, ResourceType type) {
        this.stackSize = 0;
        this.name = name;
        this.slotIndex = slotIndex;
        this.resourceType = type;
    }

    public Upgrade getCopy() {
        Upgrade upgrade = new Upgrade(this.name, this.slotIndex, this.resourceType);
        upgrade.setStackSize(this.getStackSize());
        return upgrade;
    }

    public String getName() {
        return name;
    }

    public int getStackSize() {
        return stackSize;
    }

    public int getSlotIndex() {
        return slotIndex;
    }

    public void setStackSize(int stackSize) {
        if (stackSize != this.stackSize) {
            //DebugWindow.getInstance().log("changed upgrade " + this.name + " from lvl " + this.stackSize + " to lvl " + stackSize);
            this.stackSize = stackSize;
        }
    }

    public int getCost(){
        return levels.get(stackSize).cost;
    }

    public double getEffect(){
        return levels.get(stackSize).effect;
    }

    public void addLevel(int level, double effect, int cost){
        levels.put(level, new Level(effect, cost));
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public record Level(double effect, int cost) {
    }
}
