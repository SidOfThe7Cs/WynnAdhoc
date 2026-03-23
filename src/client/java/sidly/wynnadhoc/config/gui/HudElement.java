package sidly.wynnadhoc.config.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.joml.Vector2i;
import sidly.wynnadhoc.utils.GuiUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class HudElement {
    private final HudElementData data;

    private final List<String> children = new ArrayList<>();

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

    void setScale(float scale) {
        this.data.scale = scale;
    }

    String name() {
        return data.name;
    }

    float scale() {
        return data.scale;
    }

    Vector2i renderPos() {
        return data.getRenderPos();
    }

    HudElementData data() {
        return data;
    }

    private void move(Vector2i to) {
        // TODO redo all the linking lol
        if (data.linkData != null) { // we are a child
            this.data.linkData.move(this.data, to);
        }
        if (!children.isEmpty()) {
            for (String name : children) {
                HudElement element = HudElementManager.getHudElement(name);
                // TODO this is the only place this works i dont know why need to get it to work with defaults
                if (element.data.linkData == null) element.data.linkData = new LinkData(0, 0, AnchorPoint.CENTER);
                element.data.x = to.x + element.data.linkData.getOffsetX();
                element.data.y = to.y + element.data.linkData.getOffsetY();
            }
        }
        data.move(to);
    }

    public void linkChild(HudElementData child) {
        this.children.add(child.name);
    }
    public void unLinkChild(String childName) {
        HudElementManager.getHudElement(childName).data.linkData = null;
        this.children.remove(childName);
    }
}
