package sidly.wynnadhoc.features.lootruns.enums;

// TODO fruma
public enum TrialOptions {
    AllIn("All In",
            "For the next 10 challenges, Curse effects are doubled.",
            "At the end of your run, convert each End Reward Sacrifice you have into +2 End Reward Rerolls."),
    GamblingBeast("Gambling Beast",
            "After finishing a Challenge, consume 300s (+60s per Challenge) from your timer.",
            "At the end of your run, gain +1 End Reward Reroll for each activation."),
    Hubris("Hubris",
            "For the next 10 challenges, dying will end your Lootrun.",
            "Gain +1 End Reward Reroll and +1 End Reward Sacrifice."),
    LightsOut("Lights Out",
            "Rainbow and Vibrant beacons will no longer be offered.",
            "Each challenge will reward +2 End Reward Pulls."),
    SideHustle("Side Hustle",
            "Until you open 25 chests, your Timer is limited to 1 minute, but dying will not consume any time.",
            "Gain +2 End Reward Rerolls."),
    TreasuryBill("Treasury Bill",
            "Until you gain 20 Pulls, lose a Pull every 45s.",
            "Boost your current End Reward Pulls by +70%."),
    UltimateSacrifice("Ultimate Sacrifice",
            "For the next 10 challenges, your Boons are disabled.",
            "Gain +2 End Reward Sacrifices."),
    WarmthDevourer("Warmth Devourer",
            "Until you gain 20 End Reward Pulls, consume 2 boons and 1 challenge after finishing a challenge.",
            "Gain +1 End Reward Reroll and +1 End Reward Sacrifice.");

    private final String displayName;
    private final String trial;
    private final String reward;

    TrialOptions(String displayName, String trial, String reward) {
        this.displayName = displayName;
        this.trial = trial;
        this.reward = reward;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTrial() {
        return trial;
    }

    public String getReward() {
        return reward;
    }
}
