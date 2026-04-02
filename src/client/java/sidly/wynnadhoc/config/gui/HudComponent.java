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

public abstract class HudComponent {
    private final HudComponentData data;

    public HudComponent(HudComponentData data) {
        this.data = data;
    }

    protected float stringWidth;
    protected float stringHeight;

    protected double grabDifX;
    protected double grabDifY;

    void renderHover(DrawContext drawContext) {
        // render hover tooltip
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

    void renderBackground(DrawContext drawContext) {
        if (data instanceof SubViewPort viewPort && isVisible()) {
            viewPort.render(drawContext);
        }
    }

    abstract void render(Vector2i pos, DrawContext drawContext, boolean override);

    void render(DrawContext drawContext, boolean override) {
        render(renderPos(), drawContext, override);
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
        boolean isViewPort = data instanceof SubViewPort;
        float width = isViewPort ? ((SubViewPort) data).width : stringWidth;
        float height = isViewPort ? ((SubViewPort) data).height : stringHeight;
        return mouseX >= pos.x() && mouseX < pos.x() + width && mouseY >= pos.y() && mouseY < pos.y() + height;
    }

    String name() {
        return data.name;
    }

    float scale() {
        return data.scale;
    }

    public HudComponentData data() {
        return data;
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

    private Vector2i renderPos() {
        Window window = MinecraftClient.getInstance().getWindow();
        Vector2i offset = new Vector2i(0, 0);
        int width;
        int height;
        if (data.viewPort != null) {
            offset = data.viewPort.renderPos();
            width = data.viewPort.getWidth();
            height = data.viewPort.getHeight();

        } else {
            width = window.getScaledWidth();
            height = window.getScaledHeight();
        }

        // force rendering on screen
        float xPos = width * (data.x < 0 ? 0 : data.x) + offset.x;
        float yPos = height * (data.y < 0 ? 0 : data.y) + offset.y;
        /*
        if (xPos + stringWidth > width) xPos = width - stringWidth;
        if (yPos + stringHeight > height) yPos = height - stringHeight;
         */

        return new Vector2i((int) xPos, (int) yPos);
    }

    private void move(Vector2i to) {
        Window window = MinecraftClient.getInstance().getWindow();
        int width = window.getScaledWidth();
        int height = window.getScaledHeight();

        data.x = (float) to.x / width;
        data.y = (float) to.y / height;
    }

    protected void setViewPort(SubViewPort viewPort) {
        data.viewPort = viewPort;
    }
}
