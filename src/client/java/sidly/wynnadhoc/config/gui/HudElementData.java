package sidly.wynnadhoc.config.gui;

import com.google.gson.annotations.Expose;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import org.joml.Vector2f;
import org.joml.Vector2i;
import sidly.wynnadhoc.WynnAdhocClient;

public class HudElementData {
    @Expose
    protected String name;
    @Expose
    protected float x;
    @Expose
    protected float y;
    @Expose
    protected float xOffset;
    @Expose
    protected float yOffset;
    @Expose
    protected float scale;
    @Expose
    protected AnchorPoint anchor;
    @Expose
    protected LinkData linkData;

    public HudElementData(String name, int x, int y, float scale) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.anchor = AnchorPoint.TOP_LEFT;
    }

    public HudElementData(String name, int x, int y, float scale, AnchorPoint anchor) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.anchor = anchor;
    }

    public Vector2i getRenderPos() {
        Window window = MinecraftClient.getInstance().getWindow();
        int width = window.getScaledWidth();
        int height = window.getScaledHeight();
        Vector2f anchorVec = anchor.getLocation();

        float realXOffset = width * (this.x > 1 ? 0 : this.x) + this.xOffset;
        float realYOffset = height * (this.y > 1 ? 0 : this.y) + this.yOffset;

        float xPos = width * anchorVec.x + realXOffset;
        float yPos = height * anchorVec.y + realYOffset;

        return new Vector2i((int)  xPos, (int) yPos);
    }

    public void move(Vector2i to) {
        AnchorPoint.AnchorPointResult newAnchor = AnchorPoint.findClosestWithOffset(to);
        this.anchor = newAnchor.anchor();
        this.xOffset = newAnchor.offset().x;
        this.yOffset = newAnchor.offset().y;
    }

    public void updateDisplay() {
        HudElement hudElement = HudElementManager.getHudElement(name);
        if (hudElement != null) hudElement.updateDisplay();
        else WynnAdhocClient.LOGGER.error("Hud element with name " + name + " not found");
    }

    public HudElement getElement() {
        return HudElementManager.getHudElement(name);
    }

    @Override
    public String toString() {
        return "HudElementData{" +
                "name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", scale=" + scale +
                ", linkData=" + linkData +
                '}';
    }
}
