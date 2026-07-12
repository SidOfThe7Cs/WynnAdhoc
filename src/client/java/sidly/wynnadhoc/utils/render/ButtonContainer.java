package sidly.wynnadhoc.utils.render;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ButtonContainer {
    public static class ToggleButton {
        private final ButtonWidget button;
        private final Text text;
        private boolean shown;
        private final List<String> aliases = new ArrayList<>();

        public ToggleButton(ButtonWidget button, Text text, boolean shown) {
            this.button = button;
            this.text = text;
            this.shown = shown;
        }

        public ToggleButton(ButtonWidget button, Text text) {
            this(button, text, false);
        }

        public boolean matches(String s) {
            if (text.getString().contains(s)) return true;
            return aliases.stream().anyMatch(a -> a.contains(s));
        }

        public void addAlias(String s) {
            aliases.add(s);
        }

        public ButtonWidget getButton() {
            button.setMessage(Text.literal(shown ? "Click to Hide" : "Click to Show"));
            return button;
        }

        public void onClick(Click click, boolean doubled) {
            this.shown = !this.shown;
            button.onClick(click, doubled);
        }

        public boolean isToggled() {
            return shown;
        }

        public Text getText() {
            return text;
        }

        public void setToggled(boolean toggled) {
            this.shown = toggled;
        }
    }

    private static final float SCROLL_FACTOR = 15f;

    private final List<ToggleButton> buttons = new ArrayList<>();
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

    public ToggleButton addButton(Text text, ButtonWidget.PressAction pressAction, int height) {
        ButtonWidget button = new ButtonWidget.Builder(Text.literal(""), pressAction).size(110 - spacing * 2, height).build();
        ToggleButton buttonData = new ToggleButton(button, text);
        buttons.add(buttonData);
        buttonHeightSum += spacing + button.getHeight();
        return buttonData;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float partialTick, Screen parent) {
        if (buttons.isEmpty()) return;
        backgroundTexture.enableScissor(context, parent);

        int yOffset = spacing + scrollAmount;
        for (ToggleButton cButton : buttons) {
            ButtonWidget button = cButton.getButton();
            if (filterString.isEmpty() || cButton.matches(filterString)) {
                button.visible = true;
                int xPos = backgroundTexture.getDrawableX(parent) + backgroundTexture.getDrawableWidth() - button.getWidth() - spacing;
                int yPos = backgroundTexture.getDrawableY(parent) + yOffset;
                button.setPosition(xPos, yPos);
                context.drawText(parent.getTextRenderer(), cButton.text, backgroundTexture.getDrawableX(parent) + spacing, yPos, Color.WHITE.getRGB(), true);
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
        Optional<ToggleButton> toClick = buttons.stream()
                .filter(b -> b.getButton().visible)
                .filter(b -> b.getButton().isHovered())
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

    public List<ToggleButton> getSelected() {
        return buttons.stream().filter(ToggleButton::isToggled).toList();
    }

    public void hideAll() {
        buttons.forEach(b -> b.shown = false);
    }

    public void showAll() {
        buttons.forEach(b -> b.shown = true);
    }
}
