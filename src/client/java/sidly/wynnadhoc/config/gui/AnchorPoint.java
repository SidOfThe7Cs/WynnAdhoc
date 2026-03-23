package sidly.wynnadhoc.config.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;

public enum AnchorPoint {
    TOP_LEFT(0, 0),
    TOP_RIGHT(1, 0),
    BOTTOM_LEFT(0, 1),
    BOTTOM_RIGHT(1, 1),
    LEFT_CENTER(0, 0.5f),
    RIGHT_CENTER(1, 0.5f),
    TOP_CENTER(0.5f, 0),
    BOTTOM_CENTER(0.5f, 1),
    CENTER(0.5f, 0.5f);

    private final Vector2f location;

    public Vector2f getLocation() {
        return location;
    }

    AnchorPoint(float x, float y) {
        this.location = new Vector2f(x, y);
    }

    public static AnchorPointResult findClosestWithOffset(Vector2i screenPos) {
        Window window = MinecraftClient.getInstance().getWindow();
        int screenWidth = window.getScaledWidth();
        int screenHeight = window.getScaledHeight();
        AnchorPoint closest = null;
        double closestDistance = Double.MAX_VALUE;
        Vector2f closestOffset = new Vector2f();
        Vector2f target = new Vector2f(screenPos);
        for (AnchorPoint anchor : values()) {
            if (anchor == CENTER) continue;
            Vector2f scaledAnchorPoint = new Vector2f(screenWidth * anchor.getLocation().x, screenHeight * anchor.getLocation().y);
            double distance = scaledAnchorPoint.distanceSquared(target);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = anchor;
                target.sub(scaledAnchorPoint, closestOffset);
            }
        }
        return new AnchorPointResult(closest != null ? closest : TOP_LEFT, closestOffset);
    }

    public record AnchorPointResult(AnchorPoint anchor, Vector2f offset) { }
}
