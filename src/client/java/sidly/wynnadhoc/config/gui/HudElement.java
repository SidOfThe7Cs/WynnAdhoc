package sidly.wynnadhoc.config.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import sidly.wynnadhoc.utils.GuiUtils;

import java.awt.*;
import java.util.List;

public abstract class HudElement {
    private final HudElementData data;

    public HudElement(HudElementData data) {
        this.data = data;
    }

    protected float stringWidth;
    protected float stringHeight;

    protected double grabDifX;
    protected double grabDifY;

    void render(DrawContext drawContext, boolean override) {
        Pair<Double, Double> mousePos = GuiUtils.getScaledMousePos();
        if (mousePos == null) return;
        if (isVisible() && isHovering(mousePos.getLeft(), mousePos.getRight())) {
            List<Text> tooltip = getHoverTooltip();
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            for (int i = 0; i < tooltip.size(); i++) {
                drawContext.drawText(
                        textRenderer,
                        tooltip.get(i),
                        (int) (data.x + 2 + (stringWidth)),
                        data.y + i * textRenderer.fontHeight,
                        Color.white.getRGB(),
                        true);
            }
        }
    }

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

    List<Text> getHoverTooltip() {
        return List.of();
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
