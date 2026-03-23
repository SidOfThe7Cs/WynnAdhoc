package sidly.wynnadhoc.config.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.joml.Vector2i;
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
            Vector2i pos = renderPos();
            for (int i = 0; i < tooltip.size(); i++) {
                drawContext.drawText(
                        textRenderer,
                        tooltip.get(i),
                        (int) (pos.x + 2 + (stringWidth)),
                        pos.y + i * textRenderer.fontHeight,
                        Color.white.getRGB(),
                        true);
            }
        }
    }

    abstract void updateDisplay();

    abstract boolean isVisible();

    void onMouseDragged(double mouseX, double mouseY) {
        move(new Vector2i((int) (mouseX + grabDifX), (int) (mouseY + grabDifY)));
    }

    void onMouseClicked(Click click, boolean doubled) {
        Vector2i pos = renderPos();
        grabDifX = pos.x() - click.x();
        grabDifY = pos.y() - click.y();
    }

    void onMouseReleased() {
    }

    List<Text> getHoverTooltip() {
        return List.of();
    }

    boolean isHovering(double mouseX, double mouseY) {
        Vector2i pos = renderPos();
        return mouseX >= pos.x() && mouseX < pos.x() + stringWidth && mouseY >= pos.y() && mouseY < pos.y() + stringHeight;
    }

    String name() {
        return data.name;
    }

    float scale() {
        return data.scale * (float) MinecraftClient.getInstance().getWindow().getWidth() / 1920;
    }

    boolean setScale(double verticalAmount) {
        float currentScale = data.scale;
        float newScale = (float) (currentScale + verticalAmount / 5);
        if (newScale > 0.3) {
            this.data.scale = newScale;
            return true;
        }
        return false;
    }

    Vector2i renderPos() {
        Window window = MinecraftClient.getInstance().getWindow();
        int width = window.getScaledWidth();
        int height = window.getScaledHeight();

        // force rendering on screen
        float xPos = width * (data.x < 0 ? 0 : data.x);
        if (xPos + stringWidth > width) xPos = width - stringWidth;
        float yPos = height * (data.y < 0 ? 0 : data.y);
        if (yPos + stringHeight > height) yPos = height - stringHeight;

        return new Vector2i((int) xPos, (int) yPos);
    }

    private void move(Vector2i to) {
        Window window = MinecraftClient.getInstance().getWindow();
        int width = window.getScaledWidth();
        int height = window.getScaledHeight();

        data.x = (float) to.x / width;
        data.y = (float) to.y / height;
    }
}
