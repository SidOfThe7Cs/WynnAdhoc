package sidly.wynnadhoc.config.gui;

import com.google.gson.annotations.Expose;
import sidly.wynnadhoc.WynnAdhocClient;

public class HudElementData {
    @Expose
    protected String name;
    @Expose
    protected int x;
    @Expose
    protected int y;
    @Expose
    protected float scale;
    @Expose
    protected LinkData linkData;

    public HudElementData(String name, int x, int y, float scale) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.scale = scale;
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
