package sidly.wynnadhoc.config.gui;

import com.google.gson.annotations.Expose;
import sidly.wynnadhoc.WynnAdhocClient;

public class HudComponentData {
    @Expose
    protected String name;
    @Expose
    protected float x; // 0-1, % of screen
    @Expose
    protected float y; // 0-1, % of screen
    @Expose
    protected float scale;
    @Expose
    protected SubViewPort viewPort;

    public HudComponentData(String name, float x, float y, float scale) {
        this(name, x, y, scale, null);
    }

    public HudComponentData(String name, float x, float y, float scale, SubViewPort viewPort) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.viewPort = viewPort;
    }

    public void updateDisplay() {
        HudComponent hudComponent = HudElementManager.getHudElement(this);
        if (hudComponent != null) hudComponent.updateDisplay();
        else WynnAdhocClient.LOGGER.error("Hud element with name not found");
    }

    @Override
    public String toString() {
        return "HudElementData{" +
                ", x=" + x +
                ", y=" + y +
                ", scale=" + scale +
                '}';
    }
}
