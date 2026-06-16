package sidly.wynnadhoc.features.lootruns.enums;

import sidly.wynnadhoc.features.lootruns.BeaconEffect;

public enum BeaconColor {
    Blue("§9", new BeaconEffect(100, 200, 300, 400)),
    Purple("§5", new BeaconEffect(2, 4, 6, 8)),
    Yellow("§e", new BeaconEffect(2, 3, 4, 5)),
    Aqua("§b", new BeaconEffect(0, 0, 0, 0)),
    Pink("§d", new BeaconEffect(2, 3, 4, 5)),
    Orange("§6", new BeaconEffect(10, 15, 20, 25)),
    Green("§a", new BeaconEffect(210, 330, 450, 570)),
    DarkGrey("§8", 1, "Dark Grey", new BeaconEffect(5, 10, 15, 20)),
    White("§f", 1, new BeaconEffect(15, 20, 25, 30)),
    Grey("§7", 3, new BeaconEffect(3, 4, 5, 5)),
    Red("§c", new BeaconEffect(6, 9, 12, 15)),
    Rainbow("§d", new BeaconEffect(10, 20, 30, 40)),
    Crimson("§4", 2, new BeaconEffect(2, 3, 4, 4)),
    Obscured("§0", new BeaconEffect(-1, -1, -1, -1));

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

    public int getBeaconEffect(boolean isVibrant, AquaStatus isAqua) {
        return effect.getBeaconEffect(isVibrant, isAqua);
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
