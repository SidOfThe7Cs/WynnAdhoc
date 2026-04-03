package sidly.wynnadhoc.features.lootruns.enums;

public enum BeaconColor {
    Blue("§9"),
    Purple("§5"),
    Yellow("§e"),
    Aqua("§b"),
    Orange("§6"),
    Green("§a"),
    DarkGrey("§8", 1, "Dark Grey"),
    White("§f", 1),
    Grey("§7", 3),
    Red("§c"),
    Rainbow("§d"),
    Crimson("§4", 2);

    private final int max;
    private final String colorCode;
    private final String displayName;

    BeaconColor(String code, int max, String displayName) {
        this.max = max;
        this.colorCode = code;
        this.displayName = displayName;
    }

    BeaconColor(String code, int max) {
        this.max = max;
        this.colorCode = code;
        this.displayName = name();
    }

    BeaconColor(String code) {
        this.max = -1;
        this.colorCode = code;
        this.displayName = name();
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
