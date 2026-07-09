package sidly.wynnadhoc.utils.render;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ButtonContainer {
    private static final float SCROLL_FACTOR = 15f;

    private final List<ButtonWidget> buttons = new ArrayList<>();
    private final TextureInfo backgroundTexture;
    private final int spacing;

    private String filterString = "";
    private int scrollAmount = 0;
    private int buttonHeightSum = 0;

    public ButtonContainer(TextureInfo backgroundTexture, int spacing) {
        this.backgroundTexture = backgroundTexture;
        this.spacing = spacing;
    }

    public void setFilterString(String filterString) {
        this.scrollAmount = 0;
        this.filterString = filterString;
    }

    public void addButton(Text text, ButtonWidget.PressAction pressAction, int height) {
        ButtonWidget button = new ButtonWidget.Builder(text, pressAction).size(backgroundTexture.getDrawableWidth() - spacing * 2, height).build();
        buttons.add(button);
        buttonHeightSum += spacing + button.getHeight();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float partialTick, Screen parent) {
        if (buttons.isEmpty()) return;
        backgroundTexture.enableScissor(context, parent);

        int yOffset = spacing + scrollAmount;
        for (ButtonWidget button : buttons) {
            if (filterString.isEmpty() || button.getMessage().getString().contains(filterString)) {
                button.visible = true;
                button.setPosition(backgroundTexture.getDrawableX(parent) + spacing, backgroundTexture.getDrawableY(parent) + yOffset);
                button.render(context, mouseX, mouseY, partialTick);
                yOffset += button.getHeight() + spacing;
            } else button.visible = false;
        }
        buttonHeightSum = yOffset - scrollAmount;
        backgroundTexture.disableScissor(context);
    }

    public void renderBackground(DrawContext context, Screen parent) {
        backgroundTexture.draw(context, parent);
    }

    public boolean onMouseClicked(Click click, boolean isDoubleClick) {
        Optional<ButtonWidget> toClick = buttons.stream()
                .filter(b -> b.visible)
                .filter(ClickableWidget::isHovered)
                .findFirst();

        if (toClick.isPresent()) {
            toClick.get().onClick(click, isDoubleClick);
            return true;
        } else return false;
    }

    public boolean onMouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        int scrollAmount = (int) (deltaY * SCROLL_FACTOR);
        int minValue = Math.min(0, -buttonHeightSum + backgroundTexture.getDrawableHeight());
        this.scrollAmount = Math.clamp(this.scrollAmount + scrollAmount, minValue, 0);
        return true;
    }
}
