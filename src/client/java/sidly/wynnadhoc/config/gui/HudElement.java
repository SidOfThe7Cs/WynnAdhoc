package sidly.wynnadhoc.config.gui;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;

public abstract class HudElement {
    private final HudElementData data;

    public HudElement(HudElementData data) {
        this.data = data;
    }

    protected float stringWidth;
    protected float stringHeight;

    protected double grabDifX;
    protected double grabDifY;

    abstract void render(DrawContext drawContext, boolean override);

    abstract void updateDisplay();

    abstract boolean isVisible();

    void onMouseDragged(double mouseX, double mouseY) {
        this.data.x = (int) (mouseX + grabDifX);
        this.data.y = (int) (mouseY + grabDifY);
    }

    void onMouseClicked(Click click, boolean doubled) {
        grabDifX = x() - click.x();
        grabDifY = y() - click.y();
    }

    void onMouseReleased() {
    }

    boolean isHovering(double mouseX, double mouseY) {
        return mouseX >= x() && mouseX < x() + stringWidth && mouseY >= y() && mouseY < y() + stringHeight;
    }

    void setScale(float scale) {
        this.data.scale = scale;
    }

    String name() {
        return data.name;
    }

    float scale() {
        return data.scale;
    }

    int x() {
        return data.x;
    }

    int y() {
        return data.y;
    }
}
