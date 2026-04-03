package sidly.wynnadhoc.utils.datatypes;

import net.minecraft.client.gui.DrawContext;
import org.joml.Vector2d;
import sidly.wynnadhoc.config.gui.Side;
import sidly.wynnadhoc.utils.render.RenderUtilsKt;

public record Box(float x1, float y1, float x2, float y2) {
    public void draw(DrawContext context, int color, int round) {
        RenderUtilsKt.drawBackground(context, (int) x1, (int) y1, (int) x2, (int) y2, color, round);
    }

    public Box extend(Side side, Vector2d dest) {
        float newX = x1;
        float newY = y1;
        float newX2 = x2;
        float newY2 = y2;

        switch (side) {
            case TOP:
                newY = (float) dest.y;
                break;
            case BOTTOM:
                newY2 = (float) dest.y;
                break;
            case LEFT:
                newX = (float) dest.x;
                break;
            case RIGHT:
                newX2 = (float) dest.x;
                break;
            case TOP_LEFT:
                newX = (float) dest.x;
                newY = (float) dest.y;
                break;
            case TOP_RIGHT:
                newX2 = (float) dest.x;
                newY = (float) dest.y;
                break;
            case BOTTOM_LEFT:
                newX = (float) dest.x;
                newY2 = (float) dest.y;
                break;
            case BOTTOM_RIGHT:
                newX2 = (float) dest.x;
                newY2 = (float) dest.y;
                break;
            default:
                return this;
        }

        return new Box(newX, newY, newX2, newY2);
    }

    public Box forceInside(Box other) {
        float x1, x2, y1, y2;
        // make sure top left is x1 and y1
        if (this.x1 > this.x2) {
            x1 = this.x2;
            x2 = this.x1;
        } else {
            x1 = this.x1;
            x2 = this.x2;
        }
        if (this.y1 > this.y2) {
            y1 = this.y2;
            y2 = this.y1;
        } else {
            y1 = this.y1;
            y2 = this.y2;
        }

        if (x1 < other.x1) x1 = other.x1;
        if (x2 > other.x2) x2 = other.x2;
        if (y1 < other.y1) y1 = other.y1;
        if (y2 > other.y2) y2 = other.y2;

        return new Box(x1, y1, x2, y2);
    }

    /**
     * Converts this box to normalized coordinates relative to a parent box
     *
     * @param parent The parent box to use as reference (0-1 space)
     * @return x, y, width, height as values 0-1 representing percentages of the parent Box
     */
    public NormalizedBox toNormalizedOf(Box parent, float scale) {
        float xNorm = (x1 - parent.x1) / parent.width();
        float yNorm = (y1 - parent.y1) / parent.height();
        float widthNorm = (width() / parent.width()) / scale;
        float heightNorm = (height() / parent.height()) / scale;
        return new NormalizedBox(xNorm, yNorm, widthNorm, heightNorm);
    }

    // Helper methods
    public float width() {
        return x2 - x1;
    }

    public float height() {
        return y2 - y1;
    }

    public boolean contains(Vector2d point) {
        return point.x >= x1 && point.x <= x2 && point.y >= y1 && point.y <= y2;
    }
}

