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
    protected float scale;

    public HudElementData(String name, float x, float y, float scale) {
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
                '}';
    }
}
