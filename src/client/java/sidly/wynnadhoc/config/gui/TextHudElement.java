package sidly.wynnadhoc.config.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import sidly.wynnadhoc.utils.ChatMessageUtils;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

public class TextHudElement extends HudElement {
    private transient String text = "";
    private transient final Supplier<Boolean> visibleCondition;
    private transient final Supplier<String> updater;
    private transient final Runnable onClick;
    private transient final Supplier<List<Text>> tooltipSupplier;

    public TextHudElement(HudElementData data, Supplier<Boolean> visibleCondition, Supplier<String> updater) {
        this(data, visibleCondition, updater, () -> {}, null);
    }
    public TextHudElement(HudElementData data, Supplier<Boolean> visibleCondition, Supplier<String> updater, Runnable onClick,  Supplier<List<Text>> tooltipSupplier) {
        super(data);
        this.visibleCondition = visibleCondition;
        this.updater = updater;
        this.onClick = onClick;
        this.tooltipSupplier = tooltipSupplier;
    }

    @Override
    public void render(DrawContext drawContext, boolean override) {
        if (text.isEmpty()) updateDisplay();
        if (isVisible() || override) {
            MinecraftClient client = MinecraftClient.getInstance();
            TextRenderer textRenderer = client.textRenderer;
            Matrix3x2fStack matrixStack = drawContext.getMatrices();
            matrixStack.pushMatrix();

            // offset keeps the top left position the same when scaling
            float offsetX = (scale() - 1) * x();
            float offsetY = (scale() - 1) * y();
            matrixStack.translate(-offsetX, -offsetY);
            matrixStack.scale(scale(), scale());

            String[] lines = text.split("\n");
            if (override && text.isEmpty()) lines[0] = name();

            int lineHeight = textRenderer.fontHeight;
            float scaledLineHeight = lineHeight * scale();

            float maxWidth = 0;
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                drawContext.drawText(textRenderer, line, x(), y() + i * lineHeight, Color.RED.getRGB(), true);
                float width = textRenderer.getWidth(line) * scale();
                if (width > maxWidth) maxWidth = width;
            }

            stringWidth = maxWidth;
            stringHeight = scaledLineHeight * lines.length;

            matrixStack.popMatrix();
        }
        super.render(drawContext, override);
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
    public void onMouseClicked(Click click, boolean doubled) {
        super.onMouseClicked(click, doubled);
        try {
            this.onClick.run();
        } catch (Exception e) {
            ChatMessageUtils.sendChatMessage("Failed to run click " + e.getMessage());
        }
    }

    @Override
    public void updateDisplay() {
        if (updater == null) {
            System.err.println("no updater set for TextHudElement " + this.name());
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
            System.err.println("no visibility condition set for TextHudElement " + this.name());
            return false;
        }
        try {
            return this.visibleCondition.get();
        } catch (Exception e) {
            return false;
        }
    }
}
