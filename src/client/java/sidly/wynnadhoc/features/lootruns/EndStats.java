package sidly.wynnadhoc.features.lootruns;

import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.LootrunConfig;
import sidly.wynnadhoc.utils.Debug;

public class EndStats {
    private int endPulls = 0;
    private int endSacs = 0;
    private int endRerolls = 0;

    private static LootrunConfig config() {
        return ConfigManager.INSTANCE.config.lootrun;
    }

    public EndStats() {
    }

    public int getEndPulls() {
        return endPulls;
    }

    public int getEndSacs() {
        return endSacs;
    }

    public int getEndRerolls() {
        return endRerolls;
    }

    public void addEndPulls(int amount) {
        WynnAdhocClient.LOGGER.info(Debug.Type.LOOTRUN, "added " + amount + " end pulls");
        this.endPulls += amount;
        LootrunCore.INSTANCE.getCurrentLootrunData().addPullsSinceLastYellow(amount);
        config().missionOverlay.updateDisplay();
        config().endRewardsOverlay.updateDisplay();
    }

    public void addEndSacs(int amount) {
        this.endSacs += amount;
        config().endRewardsOverlay.updateDisplay();
    }

    public void addEndRerolls(int amount) {
        this.endRerolls += amount;
        config().endRewardsOverlay.updateDisplay();
    }

    public void clearEndSacs() {
        this.endSacs = 0;
    }
}
