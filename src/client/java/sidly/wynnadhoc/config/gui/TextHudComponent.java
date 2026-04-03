package sidly.wynnadhoc.config.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2i;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.utils.ChatMessageUtils;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

public class TextHudComponent extends HudComponent {
    private transient String text = "";
    private transient final Supplier<Boolean> visibleCondition;
    private transient final Supplier<String> updater;
    private transient final Runnable onClick;
    private transient final Supplier<List<Text>> tooltipSupplier;

    public TextHudComponent(HudComponentData data, Supplier<Boolean> visibleCondition, Supplier<String> updater) {
        this(data, visibleCondition, updater, () -> {
        }, null);
    }

    public TextHudComponent(HudComponentData data, Supplier<Boolean> visibleCondition, Supplier<String> updater, Runnable onClick, Supplier<List<Text>> tooltipSupplier) {
        super(data);
        this.visibleCondition = visibleCondition;
        this.updater = updater;
        this.onClick = onClick;
        this.tooltipSupplier = tooltipSupplier;
    }

    // TODO have an anchor point in hudelement and use it for aligning left/right/center
    @Override
    public void render(Vector2i pos, DrawContext drawContext, boolean override) {
        super.renderBackground(drawContext);
        if (text.isEmpty()) updateDisplay();
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        Matrix3x2fStack matrixStack = drawContext.getMatrices();
        matrixStack.pushMatrix();

        // offset keeps the top left position the same when scaling
        float offsetX = (scale() - 1) * pos.x();
        float offsetY = (scale() - 1) * pos.y();
        matrixStack.translate(-offsetX, -offsetY);
        matrixStack.scale(scale(), scale());

        String[] lines = text.split("\n");
        if (override && text.isEmpty()) lines[0] = name();

        int lineHeight = textRenderer.fontHeight;
        float scaledLineHeight = lineHeight * scale();

        float maxWidth = 0;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            drawContext.drawText(textRenderer, line, pos.x(), pos.y() + i * lineHeight, Color.RED.getRGB(), true);
            float width = textRenderer.getWidth(line) * scale();
            if (width > maxWidth) maxWidth = width;
        }

        contentWidth = maxWidth;
        contentHeight = scaledLineHeight * lines.length;

        matrixStack.popMatrix();
        if (!override) super.renderHover(drawContext);
    }

    @Override
    public List<Text> getHoverTooltip() {
        try {
            if (tooltipSupplier == null) return super.getHoverTooltip();
            return tooltipSupplier.get();
        } catch (Exception e) {
            return List.of(Text.literal("Failed to get tooltip"), Text.literal(e.getMessage()));
        }
    }

    @Override
    public boolean onMouseClicked(Click click, boolean doubled, boolean editing) {
        boolean thisConsumed = super.onMouseClicked(click, doubled, editing);
        if (!editing) {
            try {
                this.onClick.run();
            } catch (Exception e) {
                ChatMessageUtils.sendChatMessage("Failed to run click " + e.getMessage());
            }
        }
        return thisConsumed;
    }

    @Override
    public void updateDisplay() {
        if (updater == null) {
            WynnAdhocClient.LOGGER.warn("no updater set for TextHudElement " + name());
            return;
        }
        try {
            this.text = this.updater.get();
        } catch (Exception e) {
            this.text = "Failed to update display\n" + e.getMessage();
        }
    }

    @Override
    public boolean isVisible() {
        if (visibleCondition == null) {
            WynnAdhocClient.LOGGER.warn("no visibility condition set for TextHudElement " + name());
            return false;
        }
        try {
            return this.visibleCondition.get();
        } catch (Exception e) {
            return false;
        }
    }
}
