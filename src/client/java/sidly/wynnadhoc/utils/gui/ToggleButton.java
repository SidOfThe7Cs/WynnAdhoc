package sidly.wynnadhoc.utils.gui;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import sidly.wynnadhoc.utils.render.TextureInfo;

import java.awt.*;

public class ToggleButton extends CustomButton {
    private final ButtonWidget button;
    protected boolean shown;

    public ToggleButton(ButtonWidget button, Text text, boolean shown) {
        super(text);
        this.button = button;
        this.shown = shown;
    }

    public ToggleButton(ButtonWidget button, Text text) {
        this(button, text, false);
    }


    public ButtonWidget getButton() {
        button.setMessage(Text.literal(shown ? "Click to Hide" : "Click to Show"));
        return button;
    }

    public boolean isToggled() {
        return shown;
    }

    public void setToggled(boolean toggled) {
        this.shown = toggled;
    }

    public void onClick(Click click, boolean doubled) {
        this.shown = !this.shown;
        button.onClick(click, doubled);
    }

    @Override
    public boolean isVisible() {
        return button.visible;
    }

    @Override
    public boolean isHovered() {
        return button.isHovered();
    }

    @Override
    public int render(DrawContext context, int mouseX, int mouseY, float partialTick, Screen parent, ButtonContainer container, int yOffset) {
        TextureInfo bt = container.backgroundTexture;
        ButtonWidget button = getButton();
        if (container.filterBySearchBarText) {
            if (container.filterString.isEmpty() || this.matches(container.filterString)) {
                button.visible = true;
                int xPos = bt.getDrawableX(parent) + bt.getDrawableWidth() - button.getWidth() - container.spacing;
                int yPos = bt.getDrawableY(parent) + yOffset;
                button.setPosition(xPos, yPos);
                context.drawText(parent.getTextRenderer(), this.text, bt.getDrawableX(parent) + container.spacing, yPos, Color.WHITE.getRGB(), true);
                button.render(context, mouseX, mouseY, partialTick);
                return button.getHeight() + container.spacing;
            } else button.visible = false;
        } else {
            if (button.visible) {
                int xPos = bt.getDrawableX(parent) + bt.getDrawableWidth() - button.getWidth() - container.spacing;
                int yPos = bt.getDrawableY(parent) + yOffset;
                button.setPosition(xPos, yPos);
                context.drawText(parent.getTextRenderer(), this.text, bt.getDrawableX(parent) + container.spacing, yPos, Color.WHITE.getRGB(), true);
                button.render(context, mouseX, mouseY, partialTick);
                return button.getHeight() + container.spacing;
            }
        }
        return 0;
    }
}
