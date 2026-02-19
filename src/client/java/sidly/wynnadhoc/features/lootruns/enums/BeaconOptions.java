package sidly.wynnadhoc.features.lootruns.enums;

import sidly.wynnadhoc.features.lootruns.Core;
import sidly.wynnadhoc.utils.FormatUtils;

public enum BeaconOptions {
    VibrantBlueBeacon("Vibrant Blue Beacon", BeaconColor.Blue),
    VibrantPurpleBeacon("Vibrant Purple Beacon", BeaconColor.Purple),
    VibrantYellowBeacon("Vibrant Yellow Beacon", BeaconColor.Yellow),
    VibrantAquaBeacon("Vibrant Aqua Beacon", BeaconColor.Aqua),
    VibrantOrangeBeacon("Vibrant Orange Beacon", BeaconColor.Orange),
    VibrantGreenBeacon("Vibrant Green Beacon", BeaconColor.Green),
    VibrantDarkGreyBeacon("Vibrant Dark Grey Beacon", BeaconColor.DarkGrey),
    VibrantWhiteBeacon("Vibrant White Beacon", BeaconColor.White),
    VibrantGreyBeacon("Vibrant Grey Beacon", BeaconColor.Grey),
    VibrantRedBeacon("Vibrant Red Beacon", BeaconColor.Red),
    VibrantRainbowBeacon("Vibrant Rainbow Beacon", BeaconColor.Rainbow),
    VibrantCrimsonBeacon("Vibrant Crimson Beacon", BeaconColor.Crimson),

    BlueBeacon("Blue Beacon", BeaconColor.Blue),
    PurpleBeacon("Purple Beacon", BeaconColor.Purple),
    YellowBeacon("Yellow Beacon", BeaconColor.Yellow),
    AquaBeacon("Aqua Beacon", BeaconColor.Aqua),
    OrangeBeacon("Orange Beacon", BeaconColor.Orange),
    GreenBeacon("Green Beacon", BeaconColor.Green),
    DarkGreyBeacon("Dark Grey Beacon", BeaconColor.DarkGrey),
    WhiteBeacon("White Beacon", BeaconColor.White),
    GreyBeacon("Grey Beacon", BeaconColor.Grey),
    RedBeacon("Red Beacon", BeaconColor.Red),
    RainbowBeacon("Rainbow Beacon", BeaconColor.Rainbow),
    CrimsonBeacon("Crimson Beacon", BeaconColor.Crimson);

    private final String displayName;
    private final BeaconColor baseColor;

    BeaconOptions(String displayName, BeaconColor baseColor) {
        this.displayName = displayName;
        this.baseColor = baseColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BeaconColor getBaseColor() {
        return baseColor;
    }

    // takes in a chat message and detects any beacon options in it and adds then to the current options
    public static void getMatches(String message) { // this name is so bad, and it should just be different
        for (BeaconOptions opt : values()) {
            String[] parts = FormatUtils.splitByAnySpecialChar(message);
            for (String part : parts) {
                if (part.equals(opt.displayName)) {
                    boolean alreadyExists = Core.getCurrentLootrunData().getCurrentBeaconOptions().stream()
                            .anyMatch(existing -> existing.baseColor.equals(opt.baseColor));

                    if (!alreadyExists) {
                        Core.getCurrentLootrunData().getCurrentBeaconOptions().add(opt);
                        // TODO update display Config.updateHudElement(HudElements.Beacons);
                    }
                }
            }
        }
    }
}
