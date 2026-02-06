package sidly.wynnadhoc.lootruns;

public enum MissionOptions {
    // should work
    CleansingGreed("Cleansing Greed", "After opening a Flying Chest, remove one of your Curses."),
    //
    HighRoller("High Roller", "Gain +10 Pulls and +1 End Reward Reroll"),
    // N/A
    Materialism("Materialism", "All Challenges now additionally have the Yellow Beacon effect."),
    Hoarder("Hoarder", "After every time you are offered 30 items from Chests, get a random Boon."),
    JestersTrick("Jester's Trick", "After being offered 20 Items from Flying Chests, randomly get +3 Pulls, +1 Boon, +1 Curse, or +30s."),
    // TODO
    InterestScheme("Interest Scheme", "Gaining 2 Pulls adds an additional Flying Chest to completing your next Yellow Beacon. (Max 12)"),
    ThrillSeeker("Thrill Seeker", "Red Beacons will reward 1 Boon out of 2."),
    // N/A
    OrphionsGrace("Orphion's Grace", "Boons are now 50% more effective."),
    // descriptoin different in game?
    Gourmand("Gourmand", "When you get a Boon, get +1 Beacon Reroll."),
    //
    Porphyrophobia("Porphyrophobia", "Getting offered a Purple Beacon gives +1 Curse. Purple Beacons now give twice as many Pulls."),
    // should work
    CleansingRitual("Cleansing Ritual", "After finishing a Challenge, consume 1 Curse to gain +1 Challenge."),
    Equilibrium("Equilibrium", "After getting 2 Curses, gain a random Boon."),
    InnerPeace("Inner Peace", "Curses are now half as effective."),
    // TODO
    Optimism("Optimism", "Gain +1 Pull for every beacon you reroll."),
    //
    BackupBeat("Backup Beat", "Every time you add +360s to your timer, gain +1 Beacon Reroll."),
    // works
    Stasis("Stasis", "While picking a Beacon, your timer does not decrease. (Max 5m)"),
    // pretty sure work
    Chronokinesis("Chronokinesis", "Chests now give +1 Pull, but consume 10s (+5s per chest) from your Timer. Completing a challenge reduces this penalty by -15s."),
    // pretty sure work
    GamblingBeast("Gambling Beast", "After finishing a Challenge, consume 300s from your timer and gain +1 End Reward Reroll."),
    // works
    Redemption("Redemption", "Gain +1 End Reward Sacrifice."),
    // untested should work
    CompleteChaos("Complete Chaos", "After finishing a Challenge, get an 80% chance to gain an additional random Beacon reward.");

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
