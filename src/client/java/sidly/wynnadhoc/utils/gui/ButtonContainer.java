package sidly.wynnadhoc.utils.gui;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import sidly.wynnadhoc.utils.render.TextureInfo;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class ButtonContainer {

    private static final float SCROLL_FACTOR = 15f;

    private final ConcurrentLinkedQueue<CustomButton> buttons = new ConcurrentLinkedQueue<>();

    final TextureInfo backgroundTexture;
    final int spacing;
    final boolean filterBySearchBarText;
    String filterString = "";

    private int scrollAmount = 0;
    private int buttonHeightSum = 0;

    public ButtonContainer(TextureInfo backgroundTexture, int spacing, boolean filterSearch) {
        this.backgroundTexture = backgroundTexture;
        this.spacing = spacing;
        this.filterBySearchBarText = filterSearch;
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

    public void addButton(CustomButton button) {
        buttons.add(button);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float partialTick, Screen parent) {
        if (buttons.isEmpty()) return;
        backgroundTexture.enableScissor(context, parent);

        int yOffset = spacing + scrollAmount;
        for (CustomButton cButton : buttons) {
            yOffset += cButton.render(context, mouseX, mouseY, partialTick, parent, this, yOffset);
        }
        buttonHeightSum = yOffset - scrollAmount;
        backgroundTexture.disableScissor(context);
    }

    public void renderBackground(DrawContext context, Screen parent) {
        backgroundTexture.draw(context, parent);
    }

    public boolean onMouseClicked(Click click, boolean isDoubleClick) {
        Optional<CustomButton> toClick = buttons.stream()
                .filter(CustomButton::isVisible)
                .filter(CustomButton::isHovered)
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

    public List<CustomButton> getSelected() {
        return buttons.stream().filter(button -> {
            if (button instanceof ToggleButton toggle) {
                return toggle.isToggled();
            } else return false;
        }).toList();
    }

    public void hideAll() {
        buttons.forEach(button -> {
            if (button instanceof ToggleButton toggle) {
                toggle.shown = false;
            }
        });
    }

    public void showAll() {
        buttons.forEach(button -> {
            if (button instanceof ToggleButton toggle) {
                toggle.shown = true;
            }
        });
    }

    public void cleanUnselected() {
        Collection<CustomButton> collect = buttons.stream().filter(button -> {
            if (button instanceof ToggleButton toggle) {
                return !toggle.isToggled();
            } else return false;
        }).collect(Collectors.toSet());
        buttons.removeAll(collect);
    }
}
