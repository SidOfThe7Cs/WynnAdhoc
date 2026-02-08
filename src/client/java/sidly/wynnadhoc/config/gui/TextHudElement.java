package sidly.wynnadhoc.config.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.function.Supplier;

public class TextHudElement extends HudElement {
    private transient String text = "";
    private transient final Supplier<Boolean> visibleCondition;
    private transient final Supplier<String> updater;
    private transient final Runnable onClick;

    public TextHudElement(HudElementData data, Supplier<Boolean> visibleCondition, Supplier<String> updater, Runnable onClick) {
        super(data);
        this.visibleCondition = visibleCondition;
        this.updater = updater;
        this.onClick = onClick;
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
    }

    @Override
    public void onMouseClicked(Click click, boolean doubled) {
        super.onMouseClicked(click, doubled);
        this.onClick.run();
    }

    @Override
    public void updateDisplay() {
        if (updater == null) {
            System.err.println("no updater set for TextHudElement " + this.name());
            return;
        }
        this.text = this.updater.get();
    }

    @Override
    public boolean isVisible() {
        if (visibleCondition == null) {
            System.err.println("no visibility condition set for TextHudElement " + this.name());
            return false;
        }
        return this.visibleCondition.get();
    }
}
