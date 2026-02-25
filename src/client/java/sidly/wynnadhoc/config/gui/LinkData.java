package sidly.wynnadhoc.config.gui;

import org.joml.Vector2i;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.utils.Debug;

public class LinkData {
    private int offsetX;
    private int offsetY;
    private AnchorPoint anchor; //TODO anchor points lol right click makes drop down? well actually maybe we could right click to change render order, also handle consuming mouse

    public LinkData(int x, int y, AnchorPoint anchor) {
        this.offsetX = x;
        this.offsetY = y;
        this.anchor = anchor;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void move(HudElementData self, Vector2i absolutePos) {
        Vector2i parentData = new Vector2i(self.x - offsetX, self.y - offsetY);
        offsetX = absolutePos.x - parentData.x;
        offsetY = absolutePos.y - parentData.y;
    }

    @Override
    public String toString() {
        return "LinkData{" +
                "offsetX=" + offsetX +
                ", offsetY=" + offsetY +
                ", anchor=" + anchor +
                '}';
    }
}
