package sidly.wynnadhoc.features.lootruns.enums;

public enum BeaconColor {
    Blue,
    Purple,
    Yellow,
    Aqua,
    Orange,
    Green,
    DarkGrey,
    White,
    Grey,
    Red,
    Rainbow,
    Crimson;

    /** Custom valueOf that ignores spaces and is case-insensitive */
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
