package sidly.wynnadhoc.features.lootruns.enums;

import net.minecraft.text.Text;

import java.util.List;

public enum TrialOptions {
    Imperitia("Imperitia",
            "Gain 35 pulls",
            "Spell costs increase by +5 mana every challenge completed",
            "+2 sacrifices"),
    AdrenalineJunkie("Adrenaline Junkie",
            "Gain 25 pulls",
            "Lose 1 Boon every 25s during interludes",
            "+2 rerolls"),
    DyingLight("Dying Light",
            "Gain 800% boon potency",
            "Your boons lose 5% Potency every 2.5s, one by one",
            "Rainbow beacons now grant +1 sacrifice"),
    Monochromokopia("Monochromokopia",
            "Gain 30 pulls",
            "Beacons completed will become obscured for 7 challenges",
            "Add an additional White, Gray and Dark Gray beacon to your pool"),
    Chronotrigger("Chronotrigger",
            "Complete 12 challenges",
            "You cannot gain time in any way",
            "Green beacons cleanse 7.5% of your curses to boost your pulls by 1%, max 5% (rounds up)"),
    AllIn("All In",
            "Complete 10 challenges",
            "Curse effects are doubled",
            "Convert each sacrifice into 3 rerolls"),
    GamblingBeast("Gambling Beast",
            "Dont run out of time",
            "Lose 300 + 90(n-1) seconds every n challenge completed",
            "+1 reroll every challenge completed"),
    Hubris("Hubris",
            "Complete 10 challenges",
            "Dying ends your lootrun",
            "+1 reroll, +1 sacrifice"),
    LightsOut("Lights Out",
            "Defeat 25 radiant challenge mobs",
            "Receive 2 Radiant chance curses every challenge",
            "Cleanse all Radiant curses to gain +4 Pulls per"),
    SideHustle("Side Hustle",
            "Open 30 chests",
            "Your timer is set to 75 seconds upon starting, completing, or failing a challenge",
            "+2 rerolls"),
    TreasuryBill("Treasury Bill",
            "Gain 20 end reward pulls",
            "Lose a pull every 45s",
            "Boosts current end reward pulls by 75%"),
    UltimateSacrifice("Ultimate Sacrifice",
            "Complete 10 challenges",
            "Your Boons are disabled",
            "+2 sacrifices"),
    WarmthDevourer("Warmth Devourer",
            "Gain 20 end reward pulls",
            "Lose 1 boon and one challenge every challenge completed",
            "+1 reroll, +1 sacrifice");

    private final String displayName;
    private final String objective;
    private final String penalty;
    private final String reward;

    TrialOptions(String displayName, String objective, String penalty, String reward) {
        this.displayName = displayName;
        this.objective = objective;
        this.penalty = penalty;
        this.reward = reward;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getObjective() {
        return objective;
    }

    public String getPenalty() {
        return penalty;
    }

    public String getReward() {
        return reward;
    }

    public List<Text> getDescription() {
        return List.of(
                Text.literal("Objective: " + getObjective()),
                Text.literal("Penalty: " + getPenalty()),
                Text.literal("Reward: " + getReward())
        );
    }
}
