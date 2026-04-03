package sidly.wynnadhoc.config.gui;

import net.minecraft.client.gui.Click;

public enum Side {
    TOP,
    LEFT,
    BOTTOM,
    RIGHT,
    NONE;

    public static Side from(int xSmall, int xLarge, int ySmall, int yLarge, Click click, int tolerance) {
        int distTop = Math.abs((int) click.y() - ySmall);
        int distBottom = Math.abs((int) click.y() - yLarge);
        int distLeft = Math.abs((int) click.x() - xSmall);
        int distRight = Math.abs((int) click.x() - xLarge);

        int closestDist = Math.min(
                Math.min(distTop, distBottom),
                Math.min(distLeft, distRight)
        );

        if (closestDist > tolerance) {
            return NONE;
        }

        if (closestDist == distTop) return TOP;
        if (closestDist == distBottom) return BOTTOM;
        if (closestDist == distLeft) return LEFT;
        if (closestDist == distRight) return RIGHT;

        return NONE;
    }
}
