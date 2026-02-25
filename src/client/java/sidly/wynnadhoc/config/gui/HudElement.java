package sidly.wynnadhoc.config.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.utils.Debug;
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
        move(new Vector2i((int) (mouseX + grabDifX), (int) (mouseY + grabDifY)));
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

    HudElementData data() {
        return data;
    }

    private void move(Vector2i to) {
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
        this.data.x = to.x();
        this.data.y = to.y();
    }

    public void linkChild(HudElementData child) {
        this.children.add(child.name);
    }
    public void unLinkChild(String childName) {
        HudElementManager.getHudElement(childName).data.linkData = null;
        this.children.remove(childName);
    }
}
