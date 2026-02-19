package sidly.wynnadhoc.features.war;

import com.wynntils.models.items.items.gui.TerritoryItem;
import com.wynntils.models.territories.type.TerritoryUpgrade;
import sidly.wynnadhoc.utils.DebugWindow;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Territory {
    private final String name;
    private final int emeraldsPerHour;
    private final int orePerHour;
    private final int cropsPerHour;
    private final int fishPerHour;
    private final int woodPerHour;

    private double treasuryBonus = 0;
    private boolean isMarkedAsUnknown = false;
    private boolean isHQ = false;

    private String holder = "Nobody";
    private Long heldSince = Long.MAX_VALUE; // seconds since acquired

    public TowerUpgrades towerUpgrades = new TowerUpgrades();
    public BonusUpgrades bonusUpgrades = new BonusUpgrades();

    public Territory(String name, int emeraldsPerHour, int orePerHour, int cropsPerHour, int fishPerHour, int woodPerHour) {
        this.name = name;
        this.emeraldsPerHour = emeraldsPerHour;
        this.orePerHour = orePerHour;
        this.cropsPerHour = cropsPerHour;
        this.fishPerHour = fishPerHour;
        this.woodPerHour = woodPerHour;
    }

    public Territory getCopy() {
        Territory copy = new Territory(this.name, this.emeraldsPerHour, this.orePerHour, this.cropsPerHour, this.fishPerHour, this.woodPerHour);
        copy.treasuryBonus = this.treasuryBonus;
        copy.isMarkedAsUnknown = this.isMarkedAsUnknown;
        copy.isHQ = this.isHQ;
        copy.holder = this.holder;
        copy.heldSince = this.heldSince;

        // Deep copy towerUpgrades
        copy.towerUpgrades = new TowerUpgrades();
        copy.towerUpgrades.upgrades.clear();
        for (Map.Entry<String, Upgrade> entry : this.towerUpgrades.upgrades.getAllByName().entrySet()) {
            Upgrade uCopy = entry.getValue().getCopy();
            copy.towerUpgrades.upgrades.getAllByName().put(entry.getKey(), uCopy);
        }

        // Deep copy bonusUpgrades
        copy.bonusUpgrades = new BonusUpgrades();
        copy.bonusUpgrades.upgrades.clear();
        for (Map.Entry<String, Upgrade> entry : this.bonusUpgrades.upgrades.getAllByName().entrySet()) {
            Upgrade uCopy = entry.getValue().getCopy();
            copy.bonusUpgrades.upgrades.getAllByName().put(entry.getKey(), uCopy);
        }

        return copy;
    }

    public String getName() {
        return name;
    }

    public int getEmeraldsPerHourProd() {
        double efficiency = bonusUpgrades.upgrades.getByName("Efficient Emeralds").getEffect();
        double rate = bonusUpgrades.upgrades.getByName("Emerald Rate").getEffect();
        double resourceIncrease = ((4.0 / rate) * (1.0 + efficiency / 100.0));
        resourceIncrease *= (1.0 + treasuryBonus / 100);
        return (int) (emeraldsPerHour * resourceIncrease);
    }

    public int getOrePerHourProd() {
        return (int) (orePerHour * getResourceIncrease());
    }

    public int getCropsPerHourProd() {
        return (int) (cropsPerHour * getResourceIncrease());
    }

    public int getFishPerHourProd() {
        return (int) (fishPerHour * getResourceIncrease());
    }

    public int getWoodPerHourProd() {
        return (int) (woodPerHour * getResourceIncrease());
    }

    private double getResourceIncrease(){
        double efficiency = bonusUpgrades.upgrades.getByName("Efficient Resources").getEffect();
        double rate = bonusUpgrades.upgrades.getByName("Resource Rate").getEffect();
        double resourceIncrease = ((4.0 / rate) * (1.0 + efficiency / 100.0));
        resourceIncrease *= (1.0 + treasuryBonus / 100);
        return resourceIncrease;
    }

    public Map<ResourceType, Integer> getStorageCaps() {
        Map<ResourceType, Integer> caps = new HashMap<>();

        Upgrade resStorageUpgrade = bonusUpgrades.upgrades.getByName("Larger Resource Storage");
        Upgrade emStorageUpgrade = bonusUpgrades.upgrades.getByName("Larger Emerald Storage");

        double resCap = 300;
        double emCap = 3000;

        if (isHQ()) {
            resCap += 1200;
            emCap += 2000;
        }

        resCap *= 1 + resStorageUpgrade.getEffect() / 100;
        emCap *= 1 + emStorageUpgrade.getEffect() / 100;

        caps.put(ResourceType.Emeralds, (int) emCap);
        caps.put(ResourceType.Ore, (int) resCap);
        caps.put(ResourceType.Wood, (int) resCap);
        caps.put(ResourceType.Fish, (int) resCap);
        caps.put(ResourceType.Crops, (int) resCap);

        return caps;
    }

    public int getEmeraldsPerHourConsume() {
        return bonusUpgrades.upgrades.getEmeraldsPerHourConsume() + towerUpgrades.upgrades.getEmeraldsPerHourConsume();
    }

    public int getOrePerHourConsume() {
        return bonusUpgrades.upgrades.getOrePerHourConsume() + towerUpgrades.upgrades.getOrePerHourConsume();
    }

    public int getCropsPerHourConsume() {
        return bonusUpgrades.upgrades.getCropsPerHourConsume() + towerUpgrades.upgrades.getCropsPerHourConsume();
    }

    public int getFishPerHourConsume() {
        return bonusUpgrades.upgrades.getFishPerHourConsume() + towerUpgrades.upgrades.getFishPerHourConsume();
    }

    public int getWoodPerHourConsume() {
        return bonusUpgrades.upgrades.getWoodPerHourConsume() + towerUpgrades.upgrades.getWoodPerHourConsume();
    }

    public void setUpgrade(String name, int level){
        Upgrade tu = towerUpgrades.upgrades.getByName(name);
        Upgrade bu = bonusUpgrades.upgrades.getByName(name);
        if (tu != null){
            tu.setStackSize(level);
        } else if (bu != null){
            bu.setStackSize(level);
        }
    }


    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public boolean isHQ() {
        return isHQ;
    }

    public void setHQ(boolean HQ) {
        isHQ = HQ;
    }

    public Long getHeldSince() {
        return heldSince;
    }

    public void setHeldSince(Long heldSince) {
        this.heldSince = heldSince;
    }

    public double getTreasuryBonus() {
        return treasuryBonus;
    }

    public void setTreasuryBonus(double treasuryBonus) {
        this.treasuryBonus = treasuryBonus;
    }

    public boolean isMarkedAsUnknown() {
        return isMarkedAsUnknown;
    }

    public void setMarkedAsUnknown(boolean markedAsUnknown) {
        isMarkedAsUnknown = markedAsUnknown;
    }

    public void resetToDefaults(){
        this.towerUpgrades.upgrades.clear();
        this.bonusUpgrades.upgrades.clear();
        this.setTreasuryBonus(0.0);
        this.setMarkedAsUnknown(false);
        this.setHolder("Nobody");
        this.setHeldSince(Long.MAX_VALUE);
        this.setHQ(false);
    }

    public void parseFromWynntils(TerritoryItem terr) {
        resetToDefaults();

        this.treasuryBonus = terr.getTreasuryBonus();
        this.holder = DB.GUILD_PREFIX;
        this.setHQ(terr.isHeadquarters());

        Map<TerritoryUpgrade, Integer> upgrades = terr.getUpgrades();
        for (Map.Entry<TerritoryUpgrade, Integer> entry : upgrades.entrySet()) {
            Upgrade bonus = this.bonusUpgrades.upgrades.getByName(entry.getKey().getName());
            Upgrade tower = this.towerUpgrades.upgrades.getByName(entry.getKey().getName());

            if (bonus != null) bonus.setStackSize(entry.getValue());
            else if (tower != null) tower.setStackSize(entry.getValue());
            else DebugWindow.getInstance().log(DebugWindow.Priority.ERROR,"couldnt find territory upgrade " + entry.getKey().getName());
        }

        this.setMarkedAsUnknown(false);
    }

    public static String detectDifferences(Territory t1, Territory t2){
        if ((t1 == null && t2 != null) || (t1 != null && t2 == null)) return "one is null\n";
        else if (t1 == null && t2 == null) return "both are null\n";

        StringBuilder sb = new StringBuilder();

        // Compare treasuryBonus
        if (t1.treasuryBonus != t2.treasuryBonus) {
            sb.append("treasuryBonus ").append(t1.treasuryBonus).append(" -> ").append(t2.treasuryBonus).append("\n");
        }

        /*
        // Compare holder
        if (!t1.holder.equals(t2.holder)) {
            sb.append("holder ").append(t1.holder).append(" -> ").append(t2.holder).append("\n");
        }
         */

        // Compare hourly resources
        if (t1.emeraldsPerHour != t2.emeraldsPerHour) {
            sb.append("emeraldsPerHour ").append(t1.emeraldsPerHour).append(" -> ").append(t2.emeraldsPerHour).append("\n");
        }
        if (t1.orePerHour != t2.orePerHour) {
            sb.append("orePerHour ").append(t1.orePerHour).append(" -> ").append(t2.orePerHour).append("\n");
        }
        if (t1.cropsPerHour != t2.cropsPerHour) {
            sb.append("cropsPerHour ").append(t1.cropsPerHour).append(" -> ").append(t2.cropsPerHour).append("\n");
        }
        if (t1.fishPerHour != t2.fishPerHour) {
            sb.append("fishPerHour ").append(t1.fishPerHour).append(" -> ").append(t2.fishPerHour).append("\n");
        }
        if (t1.woodPerHour != t2.woodPerHour) {
            sb.append("woodPerHour ").append(t1.woodPerHour).append(" -> ").append(t2.woodPerHour).append("\n");
        }

        // Compare towerUpgrades
        compareUpgradeContainer(sb, t1.towerUpgrades.upgrades, t2.towerUpgrades.upgrades);

        // Compare bonusUpgrades
        compareUpgradeContainer(sb, t1.bonusUpgrades.upgrades, t2.bonusUpgrades.upgrades);


        return sb.toString();
    }

    private static void compareUpgradeContainer(StringBuilder sb, UpgradeContainer c1, UpgradeContainer c2) {
        Map<String, Upgrade> map1 = c1.getAllByName();
        Map<String, Upgrade> map2 = c2.getAllByName();

        // all upgrades should always be in the map with stack size 0 they should never not be in map

        // Check all upgrades in map1
        for (String key : map1.keySet()) {
            Upgrade u1 = map1.get(key);
            Upgrade u2 = map2.get(key);

            if (u1 != null && u2 != null && u1.getStackSize() != u2.getStackSize()) {
                sb.append(key).append(" ").append(u1.getStackSize()).append(" -> ").append(u2.getStackSize()).append("\n");
            }
        }

        // check for upgrades that exist in map2 but not in map1
        for (String key : map2.keySet()) {
            if (!map1.containsKey(key)) {
                sb.append(key).append(" 0 -> ").append(map2.get(key).getStackSize()).append("\n");
            }
        }
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Territory territory = (Territory) object;
        return Objects.equals(name, territory.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return name + "\n";
    }
}
