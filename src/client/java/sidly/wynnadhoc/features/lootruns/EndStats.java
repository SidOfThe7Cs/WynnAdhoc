package sidly.wynnadhoc.features.lootruns;

import sidly.wynnadhoc.utils.DebugWindow;

public class EndStats {
    private int endPulls = 0;
    private int endSacs = 0;
    private int endRerolls = 0;

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
        DebugWindow.getInstance().log(DebugWindow.Priority.INFO,"added " + amount + " end pulls");
        this.endPulls += amount;
        Core.getCurrentLootrunData().addPullsSinceLastYellow(amount);
        // TODO update display Config.updateHudElement(HudElements.Missions);
        // TODO update display Config.updateHudElement(HudElements.Lootrun_End_Rewards);
    }

    public void addEndSacs(int amount) {
        this.endSacs += amount;
        // TODO update display Config.updateHudElement(HudElements.Lootrun_End_Rewards);
    }

    public void addEndRerolls(int amount) {
        this.endRerolls += amount;
        // TODO update display Config.updateHudElement(HudElements.Lootrun_End_Rewards);
    }

    public void clearEndSacs() {
        this.endSacs = 0;
    }
}
