package sidly.wynnadhoc.features.chests;

import java.awt.*;

public enum ChestColor {
    RED(Color.RED),
    YELLOW(Color.YELLOW),
    GREEN(Color.YELLOW),
    UNKNOWN(Color.WHITE);

    private final Color color;

    public Color asColor() {
        return color;
    }

    public static ChestColor from(Color c) {
        if (c == Color.RED) return ChestColor.RED;
        if (c == Color.YELLOW) return ChestColor.YELLOW;
        if (c == Color.GREEN) return ChestColor.GREEN;
        return ChestColor.UNKNOWN;
    }

    ChestColor(Color color) {
        this.color = color;
    }
}
