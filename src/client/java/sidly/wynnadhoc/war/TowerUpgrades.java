package sidly.wynnadhoc.war;


public class TowerUpgrades {

    public final UpgradeContainer upgrades = new UpgradeContainer();

    public TowerUpgrades(){
        int damageIndex = 11;
        upgrades.add("Damage", damageIndex, ResourceType.Ore);
        upgrades.getBySlot(damageIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(damageIndex).addLevel(1, 40, 100);
        upgrades.getBySlot(damageIndex).addLevel(2, 80, 300);
        upgrades.getBySlot(damageIndex).addLevel(3, 120, 600);
        upgrades.getBySlot(damageIndex).addLevel(4, 160, 1200);
        upgrades.getBySlot(damageIndex).addLevel(5, 200, 2400);
        upgrades.getBySlot(damageIndex).addLevel(6, 240, 4800);
        upgrades.getBySlot(damageIndex).addLevel(7, 280, 8400);
        upgrades.getBySlot(damageIndex).addLevel(8, 320, 12000);
        upgrades.getBySlot(damageIndex).addLevel(9, 360, 15600);
        upgrades.getBySlot(damageIndex).addLevel(10, 400, 19200);
        upgrades.getBySlot(damageIndex).addLevel(11, 440, 22800);

        int AttackIndex = 12;
        upgrades.add("Attack", AttackIndex, ResourceType.Crops);
        upgrades.getBySlot(AttackIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(AttackIndex).addLevel(1, 50, 100);
        upgrades.getBySlot(AttackIndex).addLevel(2, 100, 300);
        upgrades.getBySlot(AttackIndex).addLevel(3, 150, 600);
        upgrades.getBySlot(AttackIndex).addLevel(4, 220, 1200);
        upgrades.getBySlot(AttackIndex).addLevel(5, 300, 2400);
        upgrades.getBySlot(AttackIndex).addLevel(6, 400, 4800);
        upgrades.getBySlot(AttackIndex).addLevel(7, 500, 8400);
        upgrades.getBySlot(AttackIndex).addLevel(8, 620, 12000);
        upgrades.getBySlot(AttackIndex).addLevel(9, 660, 15600);
        upgrades.getBySlot(AttackIndex).addLevel(10, 740, 19200);
        upgrades.getBySlot(AttackIndex).addLevel(11, 840, 22800);

        int HealthIndex = 13;
        upgrades.add("Health", HealthIndex, ResourceType.Wood);
        upgrades.getBySlot(HealthIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(HealthIndex).addLevel(1, 50, 100);
        upgrades.getBySlot(HealthIndex).addLevel(2, 100, 300);
        upgrades.getBySlot(HealthIndex).addLevel(3, 150, 600);
        upgrades.getBySlot(HealthIndex).addLevel(4, 220, 1200);
        upgrades.getBySlot(HealthIndex).addLevel(5, 300, 2400);
        upgrades.getBySlot(HealthIndex).addLevel(6, 400, 4800);
        upgrades.getBySlot(HealthIndex).addLevel(7, 500, 8400);
        upgrades.getBySlot(HealthIndex).addLevel(8, 620, 12000);
        upgrades.getBySlot(HealthIndex).addLevel(9, 660, 15600);
        upgrades.getBySlot(HealthIndex).addLevel(10, 740, 19200);
        upgrades.getBySlot(HealthIndex).addLevel(11, 840, 22800);

        int DefenceIndex = 14;
        upgrades.add("Defence", DefenceIndex, ResourceType.Fish);
        upgrades.getBySlot(DefenceIndex).addLevel(0, 0, 0);
        upgrades.getBySlot(DefenceIndex).addLevel(1, 300, 100);
        upgrades.getBySlot(DefenceIndex).addLevel(2, 450, 300);
        upgrades.getBySlot(DefenceIndex).addLevel(3, 525, 600);
        upgrades.getBySlot(DefenceIndex).addLevel(4, 600, 1200);
        upgrades.getBySlot(DefenceIndex).addLevel(5, 650, 2400);
        upgrades.getBySlot(DefenceIndex).addLevel(6, 690, 4800);
        upgrades.getBySlot(DefenceIndex).addLevel(7, 720, 8400);
        upgrades.getBySlot(DefenceIndex).addLevel(8, 740, 12000);
        upgrades.getBySlot(DefenceIndex).addLevel(9, 760, 15600);
        upgrades.getBySlot(DefenceIndex).addLevel(10, 780, 19200);
        upgrades.getBySlot(DefenceIndex).addLevel(11, 800, 22800);
    }

}
