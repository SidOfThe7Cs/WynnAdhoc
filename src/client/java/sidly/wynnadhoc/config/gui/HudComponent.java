package sidly.wynnadhoc.config.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector2i;
import sidly.wynnadhoc.event.MouseMoveEvent;
import sidly.wynnadhoc.utils.GuiUtils;
import sidly.wynnadhoc.utils.datatypes.Box;
import sidly.wynnadhoc.utils.datatypes.ExtensionsKt;
import sidly.wynnadhoc.utils.datatypes.NormalizedBox;

import java.awt.*;
import java.util.List;

public abstract class HudComponent {
    private final HudComponentData data;

    public HudComponent(HudComponentData data) {
        this.data = data;
    }

    protected float contentWidth;
    protected float contentHeight;

    protected double grabDifX;
    protected double grabDifY;

    protected HudComponent parent;

    // TODO config
    public final static int TOLERANCE = 5;
    private final static float SCROLL_SCALE_FACTOR = 1;

    void renderHover(DrawContext drawContext) {
        // render hover tooltip
        Vector2d mousePos = GuiUtils.getScaledMousePos();
        if (isVisible() && isHovering(mousePos)) {
            List<Text> tooltip = getHoverTooltip();
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            Vector2f pos = getScaledRenderPos();
            for (int i = 0; i < tooltip.size(); i++) {
                drawContext.drawText(
                        textRenderer,
                        tooltip.get(i),
                        (int) (pos.x + 2 + (contentWidth)),
                        (int) (pos.y + i * textRenderer.fontHeight),
                        Color.white.getRGB(),
                        true);
            }
        }
    }

    abstract void render(Vector2i pos, DrawContext drawContext, boolean override);

    void render(DrawContext drawContext, boolean override) {
        render(ExtensionsKt.to2i(getScaledRenderPos()), drawContext, override);
    }

    abstract void updateDisplay();

    abstract boolean isVisible();

    boolean onMouseClicked(Click click, boolean doubled, boolean editing) {
        if (editing) {
            Vector2f pos = getScaledRenderPos();
            grabDifX = pos.x() - click.x();
            grabDifY = pos.y() - click.y();

            if (data.width != 0 && data.height != 0) {
                Side side = Side.from((int) pos.x, (int) (pos.x + getScaledWidth()), (int) pos.y, (int) (pos.y + getScaledHeight()), click, TOLERANCE);
                if (side != Side.NONE) {
                    HudElementManager.setExpanding(this, side);
                    return true;
                }
            }

            HudElementManager.setDragging(this);
            return true;
        }
        return false;
    }

    boolean onMouseScrolled(double x, double y, double verticalAmount) {
        increaseScale(verticalAmount);
        return true;
    }

    void onMouseMoved(MouseMoveEvent event) {
    }

    void moveTo(MouseMoveEvent event) {
        move(new Vector2i((int) (event.newPosScaled.x + grabDifX), (int) (event.newPosScaled.y + grabDifY)));
    }

    void expandTo(Side expandingSide, MouseMoveEvent event) {
        if (parent == null || data.width == 0 || data.height == 0) return;
        Vector2d dest = event.newPosScaled;
        Box newBounds = getBounds().extend(expandingSide, dest);
        Box parentBounds = parent.getBounds();
        NormalizedBox result = newBounds.forceInside(parentBounds).toNormalizedOf(parentBounds, scale());
        data.updateFromNormalizedBox(result);
    }

    Box getBounds() {
        Vector2f renderPos = getRenderPos();
        float x2 = (renderPos.x + getWidth());
        float y2 = (renderPos.y + getHeight());
        return new Box(renderPos.x, renderPos.y, x2, y2);
    }

    Box getScaledBounds() {
        Vector2f renderPos = getScaledRenderPos();
        int x2 = (int) (renderPos.x + getScaledWidth());
        int y2 = (int) (renderPos.y + getScaledHeight());
        return new Box(renderPos.x, renderPos.y, x2, y2);
    }

    List<Text> getHoverTooltip() {
        return List.of();
    }

    float getWidth() {
        if (data.width == 0) {
            return contentWidth;
        } else {
            Window window = MinecraftClient.getInstance().getWindow();
            if (window == null) return 0;
            float parentWidth = parent == null ? window.getScaledWidth() : parent.getWidth();
            return data.width * parentWidth;
        }
    }

    float getHeight() {
        if (data.height == 0) {
            return contentHeight;
        } else {
            Window window = MinecraftClient.getInstance().getWindow();
            if (window == null) return 0;
            float parentHeight = parent == null ? window.getScaledHeight() : parent.getHeight();
            return data.height * parentHeight;
        }
    }

    float getScaledWidth() {
        return getWidth() * scale();
    }

    float getScaledHeight() {
        return getHeight() * scale();
    }

    void renderBackground(DrawContext context) {
        if (data.background == null) return;
        getScaledBounds().draw(context, data.background, 3);
    }

    boolean isHovering(Vector2d mousePos) {
        return getScaledBounds().contains(mousePos);
    }

    String name() {
        return data.name == null ? "Un-named Hud Element" : data.name;
    }

    float scale() {
        return parent == null ? data.scale : data.scale * parent.scale();
    }

    public HudComponentData data() {
        return data;
    }

    void increaseScale(double verticalAmount) {
        float currentScale = data.scale;
        float newScale = (float) (currentScale + verticalAmount / 10 * SCROLL_SCALE_FACTOR);
        if (newScale > 0.3) {
            this.data.scale = newScale;
        }
    }

    Vector2f getRenderPos() {
        if (parent == null) return new Vector2f(0, 0);
        Vector2f offset = parent.getRenderPos();
        float width = parent.getWidth();
        float height = parent.getHeight();
        return renderPos(offset, width, height);
    }

    Vector2f getScaledRenderPos() {
        if (parent == null) return new Vector2f(0, 0);
        Vector2f offset = parent.getRenderPos();
        float width = parent.getScaledWidth();
        float height = parent.getScaledHeight();
        return renderPos(offset, width, height);
    }

    private Vector2f renderPos(Vector2f offset, float width, float height) {
        // force rendering on screen
        float xPos = width * (data.x < 0 ? 0 : data.x) + offset.x;
        float yPos = height * (data.y < 0 ? 0 : data.y) + offset.y;

        //WynnAdhocClient.LOGGER.info(Debug.Type.TEMP, "name: " + name() + " offset: " + offset + " width: " + width + " height: " + height + " x: " + xPos + " y: " + yPos + " scale: " + scale());
        /*
        if (xPos + stringWidth > width) xPos = width - stringWidth;
        if (yPos + stringHeight > height) yPos = height - stringHeight;
         */

        return new Vector2f(xPos, yPos);
    }

    void move(Vector2i to) {
        float width = parent.getWidth();
        float height = parent.getHeight();

        data.x = (float) to.x / width;
        data.y = (float) to.y / height;
    }

    protected void setParent(HudComponent viewPort) {
        this.parent = viewPort;
    }

}
