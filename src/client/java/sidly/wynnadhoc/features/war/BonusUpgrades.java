package sidly.wynnadhoc.features.war;

public class BonusUpgrades {

    public final UpgradeContainer upgrades = new UpgradeContainer();

    public BonusUpgrades() {
        int strongerMinionsIndex = 2;
        upgrades.add("Stronger Minions", strongerMinionsIndex, ResourceType.Wood);
        upgrades.getBySlot(strongerMinionsIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(strongerMinionsIndex).addLevel(1, 150, 200);
        upgrades.getBySlot(strongerMinionsIndex).addLevel(2, 200, 400);
        upgrades.getBySlot(strongerMinionsIndex).addLevel(3, 250, 800);
        upgrades.getBySlot(strongerMinionsIndex).addLevel(4, 300, 1600);

        int towerMultiAttacksIndex = 3;
        upgrades.add("Tower Multi-Attacks", towerMultiAttacksIndex, ResourceType.Fish);
        upgrades.getBySlot(towerMultiAttacksIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(towerMultiAttacksIndex).addLevel(1, 1, 4800);

        int towerAuraIndex = 4;
        upgrades.add("Tower Aura", towerAuraIndex, ResourceType.Crops);
        upgrades.getBySlot(towerAuraIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(towerAuraIndex).addLevel(1, 24, 800);
        upgrades.getBySlot(towerAuraIndex).addLevel(2, 18, 1600);
        upgrades.getBySlot(towerAuraIndex).addLevel(3, 12, 3200);

        int towerVolleyIndex = 5;
        upgrades.add("Tower Volley", towerVolleyIndex, ResourceType.Ore);
        upgrades.getBySlot(towerVolleyIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(towerVolleyIndex).addLevel(1, 20, 200);
        upgrades.getBySlot(towerVolleyIndex).addLevel(2, 15, 400);
        upgrades.getBySlot(towerVolleyIndex).addLevel(3, 10, 800);

        int gatheringExpIndex = 11;
        upgrades.add("Gathering Experience", gatheringExpIndex, ResourceType.Wood);
        upgrades.getBySlot(gatheringExpIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(gatheringExpIndex).addLevel(1, 10, 600);
        upgrades.getBySlot(gatheringExpIndex).addLevel(2, 20, 1300);
        upgrades.getBySlot(gatheringExpIndex).addLevel(3, 30, 2000);
        upgrades.getBySlot(gatheringExpIndex).addLevel(4, 40, 2700);
        upgrades.getBySlot(gatheringExpIndex).addLevel(5, 50, 3400);
        upgrades.getBySlot(gatheringExpIndex).addLevel(6, 60, 5500);
        upgrades.getBySlot(gatheringExpIndex).addLevel(7, 80, 10000);
        upgrades.getBySlot(gatheringExpIndex).addLevel(8, 100, 20000);

        int mobExpIndex = 12;
        upgrades.add("Mob Experience", mobExpIndex, ResourceType.Fish);
        upgrades.getBySlot(mobExpIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(mobExpIndex).addLevel(1, 10, 600);
        upgrades.getBySlot(mobExpIndex).addLevel(2, 20, 1200);
        upgrades.getBySlot(mobExpIndex).addLevel(3, 30, 1800);
        upgrades.getBySlot(mobExpIndex).addLevel(4, 40, 2400);
        upgrades.getBySlot(mobExpIndex).addLevel(5, 50, 3000);
        upgrades.getBySlot(mobExpIndex).addLevel(6, 60, 5000);
        upgrades.getBySlot(mobExpIndex).addLevel(7, 80, 10000);
        upgrades.getBySlot(mobExpIndex).addLevel(8, 100, 20000);

        int mobDamageIndex = 13;
        upgrades.add("Mob Damage", mobDamageIndex, ResourceType.Crops);
        upgrades.getBySlot(mobDamageIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(mobDamageIndex).addLevel(1, 10, 600);
        upgrades.getBySlot(mobDamageIndex).addLevel(2, 20, 1200);
        upgrades.getBySlot(mobDamageIndex).addLevel(3, 40, 1800);
        upgrades.getBySlot(mobDamageIndex).addLevel(4, 60, 2400);
        upgrades.getBySlot(mobDamageIndex).addLevel(5, 80, 3000);
        upgrades.getBySlot(mobDamageIndex).addLevel(6, 120, 5000);
        upgrades.getBySlot(mobDamageIndex).addLevel(7, 160, 10000);
        upgrades.getBySlot(mobDamageIndex).addLevel(8, 200, 20000);

        int pvpDamageIndex = 14;
        upgrades.add("PvP Damage", pvpDamageIndex, ResourceType.Ore);
        upgrades.getBySlot(pvpDamageIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(pvpDamageIndex).addLevel(1, 5, 600);
        upgrades.getBySlot(pvpDamageIndex).addLevel(2, 10, 1200);
        upgrades.getBySlot(pvpDamageIndex).addLevel(3, 15, 1800);
        upgrades.getBySlot(pvpDamageIndex).addLevel(4, 20, 2400);
        upgrades.getBySlot(pvpDamageIndex).addLevel(5, 25, 3000);
        upgrades.getBySlot(pvpDamageIndex).addLevel(6, 40, 5000);
        upgrades.getBySlot(pvpDamageIndex).addLevel(7, 65, 10000);
        upgrades.getBySlot(pvpDamageIndex).addLevel(8, 80, 20000);

        int xpSeekingIndex = 15;
        upgrades.add("XP Seeking", xpSeekingIndex, ResourceType.Emeralds);
        upgrades.getBySlot(xpSeekingIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(xpSeekingIndex).addLevel(1, 36000, 100);
        upgrades.getBySlot(xpSeekingIndex).addLevel(2, 66000, 200);
        upgrades.getBySlot(xpSeekingIndex).addLevel(3, 120000, 400);
        upgrades.getBySlot(xpSeekingIndex).addLevel(4, 228000, 800);
        upgrades.getBySlot(xpSeekingIndex).addLevel(5, 456000, 1600);
        upgrades.getBySlot(xpSeekingIndex).addLevel(6, 900000, 3200);
        upgrades.getBySlot(xpSeekingIndex).addLevel(7, 1740000, 6400);
        upgrades.getBySlot(xpSeekingIndex).addLevel(8, 2580000, 9600);
        upgrades.getBySlot(xpSeekingIndex).addLevel(9, 3360000, 12800);

        int tomeSeekingIndex = 16;
        upgrades.add("Tome Seeking", tomeSeekingIndex, ResourceType.Fish);
        upgrades.getBySlot(tomeSeekingIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(tomeSeekingIndex).addLevel(1, 0.15, 400);
        upgrades.getBySlot(tomeSeekingIndex).addLevel(2, 1.2, 3200);
        upgrades.getBySlot(tomeSeekingIndex).addLevel(3, 2.4, 6400);

        int emeraldSeekingIndex = 17;
        upgrades.add("Emerald Seeking", emeraldSeekingIndex, ResourceType.Wood);
        upgrades.getBySlot(emeraldSeekingIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(emeraldSeekingIndex).addLevel(1, 0.3, 200);
        upgrades.getBySlot(emeraldSeekingIndex).addLevel(2, 3, 800);
        upgrades.getBySlot(emeraldSeekingIndex).addLevel(3, 6, 1600);
        upgrades.getBySlot(emeraldSeekingIndex).addLevel(4, 12, 3200);
        upgrades.getBySlot(emeraldSeekingIndex).addLevel(5, 24, 6400);

        int largerResourceStorageIndex = 20;
        upgrades.add("Larger Resource Storage", largerResourceStorageIndex, ResourceType.Emeralds);
        upgrades.getBySlot(largerResourceStorageIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(largerResourceStorageIndex).addLevel(1, 100, 400);
        upgrades.getBySlot(largerResourceStorageIndex).addLevel(2, 300, 800);
        upgrades.getBySlot(largerResourceStorageIndex).addLevel(3, 700, 2000);
        upgrades.getBySlot(largerResourceStorageIndex).addLevel(4, 1400, 5000);
        upgrades.getBySlot(largerResourceStorageIndex).addLevel(5, 3300, 16000);
        upgrades.getBySlot(largerResourceStorageIndex).addLevel(6, 7900, 48000);

        int largerEmeraldStorageIndex = 21;
        upgrades.add("Larger Emerald Storage", largerEmeraldStorageIndex, ResourceType.Wood);
        upgrades.getBySlot(largerEmeraldStorageIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(largerEmeraldStorageIndex).addLevel(1, 100, 200);
        upgrades.getBySlot(largerEmeraldStorageIndex).addLevel(2, 300, 400);
        upgrades.getBySlot(largerEmeraldStorageIndex).addLevel(3, 700, 1000);
        upgrades.getBySlot(largerEmeraldStorageIndex).addLevel(4, 1400, 2500);
        upgrades.getBySlot(largerEmeraldStorageIndex).addLevel(5, 3300, 8000);
        upgrades.getBySlot(largerEmeraldStorageIndex).addLevel(6, 7900, 24000);

        int efficientResourcesIndex = 22;
        upgrades.add("Efficient Resources", efficientResourcesIndex, ResourceType.Emeralds);
        upgrades.getBySlot(efficientResourcesIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(efficientResourcesIndex).addLevel(1, 50, 6000);
        upgrades.getBySlot(efficientResourcesIndex).addLevel(2, 100, 12000);
        upgrades.getBySlot(efficientResourcesIndex).addLevel(3, 150, 24000);
        upgrades.getBySlot(efficientResourcesIndex).addLevel(4, 200, 48000);
        upgrades.getBySlot(efficientResourcesIndex).addLevel(5, 250, 96000);
        upgrades.getBySlot(efficientResourcesIndex).addLevel(6, 300, 192000);

        int efficientEmeraldsIndex = 23;
        upgrades.add("Efficient Emeralds", efficientEmeraldsIndex, ResourceType.Ore);
        upgrades.getBySlot(efficientEmeraldsIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(efficientEmeraldsIndex).addLevel(1, 35, 2000);
        upgrades.getBySlot(efficientEmeraldsIndex).addLevel(2, 100, 8000);
        upgrades.getBySlot(efficientEmeraldsIndex).addLevel(3, 300, 32000);

        int resourceRateIndex = 24;
        upgrades.add("Resource Rate", resourceRateIndex, ResourceType.Emeralds);
        upgrades.getBySlot(resourceRateIndex).addLevel(0, 4, 0);
        upgrades.getBySlot(resourceRateIndex).addLevel(1, 3, 6000);
        upgrades.getBySlot(resourceRateIndex).addLevel(2, 2, 18000);
        upgrades.getBySlot(resourceRateIndex).addLevel(3, 1, 32000);

        int emeraldRateIndex = 25;
        upgrades.add("Emerald Rate", emeraldRateIndex, ResourceType.Crops);
        upgrades.getBySlot(emeraldRateIndex).addLevel(0, 4, 0);
        upgrades.getBySlot(emeraldRateIndex).addLevel(1, 3, 2000);
        upgrades.getBySlot(emeraldRateIndex).addLevel(2, 2, 8000);
        upgrades.getBySlot(emeraldRateIndex).addLevel(3, 1, 32000);
    }

}
