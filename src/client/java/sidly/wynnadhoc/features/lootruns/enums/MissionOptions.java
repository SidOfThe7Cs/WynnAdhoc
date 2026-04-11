package sidly.wynnadhoc.features.lootruns.enums;

// TODO fruma
public enum MissionOptions {
    CleansingGreed("Cleansing Greed", "After opening a Flying Chest, remove one of your Curses."),
    HighRoller("High Roller", "Gain +10 Pulls and +1 End Reward Reroll"),
    Materialism("Materialism", "All Challenges now additionally have the Yellow Beacon effect."),
    Hoarder("Hoarder", "After opening 7 Chests, choose 1 Boon out of 3."),
    JestersTrick("Jester's Trick", "After being offered 30 Items from Flying Chests, randomly get +3 Pulls, +1 Boon, +1 Curse, or +30s."),
    InterestScheme("Interest Scheme", "Gaining 2 Pulls adds an additional Flying Chest to completing your next Yellow Beacon. (Max 12)"),
    ThrillSeeker("Thrill Seeker", "Red Beacons will reward 1 Boon out of 25."),
    OrphionsGrace("Orphion's Grace", "Boons are now 50% more effective."),
    OpalOffering("Opal Offering", "After getting a Curse, consume 1 Boon to gain +2 Pulls (+1 per 50% Potency)"),
    Gourmand("Gourmand", "Gain +1 Beacon Choice when you use a Beacon Reroll (Max 6). Resets when you begin a challenge. "),
    Porphyrophobia("Porphyrophobia", "Getting offered a Purple Beacon gives +1 Curse. Purple Beacons now give twice as many Pulls."),
    SacrificialRitual("Sacrificial Ritual", "After finishing a Challenge, consume 1 Pull to gain +3 Challenge."),
    RadiantHunter("Radiant Hunter", " \tDefeating a Radiant Challenge mob grants +1 Pulls (5 max per challenge)."),
    Equilibrium("Equilibrium", "Gaining a Curse will boost the Potency of your next boon by +50%. (Max +300%) "),
    InnerPeace("Inner Peace", "Curses are now half as effective."),
    Optimism("Optimism", "When a beacon is rerolled, it will not be re-offered unless you run out of options."),
    BackupBeat("Backup Beat", "Every time you add +300s to your timer, gain +1 Beacon Reroll."),
    Requiem("Requiem", "For the next 15 minutes, Enemy stat increases are voided."),
    Stasis("Stasis", "While picking a Beacon, your timer does not decrease. (Max 5m)"),
    Chronokinesis("Chronokinesis", "(Non-flying) chests now give +1 Pull, but consume 10s (+5s per chest) from your Timer. Completing a challenge reduces this penalty by -15s."),
    Redemption("Redemption", "Gain +1 End Reward Sacrifice."),
    CompleteChaos("Complete Chaos", "After finishing a Challenge, get an additional random Beacon reward. ");

    private final String displayName;
    private final String description;

    MissionOptions(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
