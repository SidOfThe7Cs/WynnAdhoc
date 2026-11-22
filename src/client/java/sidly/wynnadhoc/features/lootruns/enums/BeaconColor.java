package sidly.wynnadhoc.features.lootruns.enums;

import sidly.wynnadhoc.features.lootruns.BeaconEffect;

public enum BeaconColor {
    Blue("§9", 30, new BeaconEffect(100, 100)),
    Purple("§5", new BeaconEffect(2, 2)),
    Yellow("§e", new BeaconEffect(2, 1)),
    Aqua("§b", new BeaconEffect(0, 0)),
    Pink("§d", new BeaconEffect(2, 1)),
    Orange("§6", new BeaconEffect(10, 5)),
    Green("§a", new BeaconEffect(240, 120)),
    DarkGrey("§8", 2, "Dark Grey", new BeaconEffect(2, 1)),
    White("§f", 1, new BeaconEffect(15, 5)),
    Grey("§7", 3, new BeaconEffect(3, 1)),
    Red("§c", new BeaconEffect(6, 3)),
    Violet("§u", new BeaconEffect(10, -2)),
    Rainbow("§d", new BeaconEffect(15, 5)),
    Crimson("§4", 2, new BeaconEffect(2, 1)),
    Obscured("§0", new BeaconEffect(-1, 0));

    private final int max;
    private final String colorCode;
    private final String displayName;
    private final BeaconEffect effect;

    BeaconColor(String code, int max, String displayName, BeaconEffect effect) {
        this.max = max;
        this.colorCode = code;
        this.displayName = displayName;
        this.effect = effect;
    }

    BeaconColor(String code, int max, BeaconEffect effect) {
        this.max = max;
        this.colorCode = code;
        this.displayName = name();
        this.effect = effect;
    }

    BeaconColor(String code, BeaconEffect effect) {
        this.max = -1;
        this.colorCode = code;
        this.displayName = name();
        this.effect = effect;
    }

    public int getMax() {
        return max;
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBeaconEffect(boolean isVibrant, int aquaMultiplier) {
        return effect.getBeaconEffect(isVibrant, aquaMultiplier);
    }

    /**
     * Custom valueOf that ignores spaces and is case-insensitive
     */
    public static BeaconColor fromString(String name) {
        if (name == null) return null;

        String cleaned = name.replaceAll("\\s+", ""); // remove spaces
        for (BeaconColor color : values()) {
            if (color.name().equalsIgnoreCase(cleaned)) {
                return color;
            }
        }
        throw new IllegalArgumentException("No enum constant for '" + name + "'");
    }

}
