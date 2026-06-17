package sidly.wynnadhoc.features.lootruns.enums;

public enum MissionOptions {
    CleansingGreed("Cleansing Greed", "After opening a Flying Chest, remove one of your Curses."),
    HighRoller("High Roller", "Gain +10 Pulls and +1 End Reward Reroll"),
    Materialism("Materialism", "Spawns 2 flying chests after every challenge completed."),
    Hoarder("Hoarder", "After opening 8 flying chests, choose 1 boon at 300% potency from 2 options."),
    JestersTrick("Jester's Trick", "After being offered 25 Items from Flying Chests, randomly get +3 Pulls, +1 Boon, +1 Curse, or +30s."),
    InterestScheme("Interest Scheme", "Gaining 2 Pulls adds an additional Flying Chest to completing your next Yellow Beacon. (Max 12)"),
    ThrillSeeker("Thrill Seeker", "Gain +1 Pull per Red Beacon challenge completed. This amount increases by +1 per 6 challenges completed (max +3), which resets upon taking a Green Beacon."),
    OrphionsGrace("Orphion's Grace", "Boons are now 50% more effective."),
    OpalOffering("Opal Offering", "After getting a Curse, consume 1 Boon to gain +2 Pulls (+1 per 50% Potency)"),
    Gourmand("Gourmand", "Gain 2 beacon choices for this challenge for every beacon reroll used."),
    Porphyrophobia("Porphyrophobia", "Getting offered a Purple Beacon gives +1 Curse. Purple Beacons now give twice as many Pulls."),
    SacrificialRitual("Sacrificial Ritual", "After finishing a Challenge, consume 1 Pull to gain +3 Challenge."),
    RadiantHunter("Radiant Hunter", "Defeating a Radiant Challenge mob grants +1 Pulls (5 max per challenge), Cleanse 1 Radiant Chance curse per 15 pulls obtained this way."),
    Equilibrium("Equilibrium", "Gaining a Curse will boost the Potency of your next boon by +50%. (Max +600%) "),
    InnerPeace("Inner Peace", "Curses are now half as effective."),
    Optimism("Optimism", "Upon rerolling a beacon, it will not be re-offered until no more beacons can be offered."),
    BackupBeat("Backup Beat", "Every time you add +300s to your timer, gain +1 Beacon Reroll."),
    Requiem("Requiem", "For the next 15 minutes, Enemy stat increases are voided."),
    Stasis("Stasis", "While picking a Beacon, your timer does not decrease. (Max 5m)"),
    Chronokinesis("Chronokinesis", "(Non-flying) chests now give +1 Pull, but consume 10s (+5s per chest) from your Timer. Completing a challenge reduces this penalty by -15s."),
    Redemption("Redemption", "Gain +1 End Reward Sacrifice."),
    RouteIndigo("Route Indigo", "Purple and Blue beacons are obscured, but are always Greatly Empowered (equivalent to a Vibrant Aqua boost)"),
    Ostinato("Ostinato", "Taking a Boon type you already have will grant +2 Pulls per duplicate you own. Your Boons will be reduced in potency by 50% for every duplicate type."),
    BelezaPura("Beleza Pura", "If an Aqua Beacon is offered, all beacons offered alongside it will be boosted by that same Aqua."),
    HighSpirits("High Spirits ", "Gain +50% Vibrancy chance."),
    KnifeEdge("Knife Edge", "Add +1 Crimson Beacon to your beacon pool."),
    KingsCourt("King's Court", "Gain +50% Vibrancy chance."),
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
