package sidly.wynnadhoc.config.gui;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import org.joml.Vector2i;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.GuiConfig;
import sidly.wynnadhoc.event.MouseMoveEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiElement extends HudComponent {
    private static GuiConfig config() {
        return ConfigManager.INSTANCE.config.gui;
    }

    private final List<HudComponent> children = new ArrayList<>();
    private boolean editMode = false;

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public GuiElement(HudComponentData viewPort, HudComponent... children) {
        super(viewPort);
        for (HudComponent child : children) {
            addChild(child);
        }
    }

    public void addChild(HudComponent newChild) {
        newChild.setParent(this);
        float area = newChild.getWidth() * newChild.getHeight(); // TODO reorder when size changes

        // Find insertion position based on area
        int insertIndex = 0;
        for (HudComponent existingChild : children) {
            float existingArea = existingChild.getWidth() * existingChild.getHeight();
            if (area < existingArea) break;
            insertIndex++;
        }

        children.add(insertIndex, newChild);
    }

    private void sortChildren() {
        children.sort(Comparator.comparingDouble(c -> c.getWidth() * c.getHeight()));
    }

    public HudComponent getHoveredChild(double x, double y) {
        return children.stream()
                .filter(child -> child.isHovering(x, y))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void render(Vector2i pos, DrawContext drawContext, boolean override, float extraScale) {
        if (isVisible() || override) {
            super.renderBackground(drawContext);
            children.forEach(child -> child.render(drawContext, editMode, scale(extraScale)));
            if (!editMode) super.renderHover(drawContext);
        }
    }

    @Override
    void updateDisplay() {
        children.forEach(HudComponent::updateDisplay);
    }

    @Override
    boolean isVisible() { // TODO store visibility coondition
        return true;
    }

/*
    // TODO we want all parents to have edit mode on if a child does
    // keybind toggles global editmode right click children to swap to editing them escape backs out one layer
    @Override
    public boolean keyPressed(KeyInput input) {
        if (sameKeyPress) sameKeyPress = false;
        else if (input.isEscape() || config().guiEditorKeybind == input.getKeycode()) {
            editMode = false;
            ConfigManager.INSTANCE.save();
            return true;
        }

        return super.keyPressed(input);
    }

    @Override
    public boolean onMouseReleased(Click click) {
        if (click.button() == 0) { // Left click
            HudComponent hoveredChild = getHoveredChild(click.x(), click.y());
            if (hoveredChild != null) {
                hoveredChild.onMouseReleased(click, doubled);
            }
        }
        return super.mouseReleased(click);
    }

 */

    @Override
    public boolean onMouseClicked(Click click, boolean doubled, boolean editing) {
        boolean thisConsumed = super.onMouseClicked(click, doubled, editing);
        HudComponent hoveredChild = getHoveredChild(click.x(), click.y());
        boolean childConsumed = false;
        if (hoveredChild != null) {
            childConsumed = hoveredChild.onMouseClicked(click, doubled, isEditMode());
        }
        return thisConsumed || childConsumed;
    }

    @Override
    public void onMouseMoved(MouseMoveEvent event) {
        // if we are edit mode render the name of the hovered child
        if (!editMode) {
            super.onMouseMoved(event);
            return;
        }

        HudComponent hoveredChild = getHoveredChild(event.newPosScaled.x, event.newPosScaled.y);
        if (hoveredChild != null) {
            HudElementManager.setDescription(hoveredChild.name());
            hoveredChild.onMouseMoved(event);
        } else {
            if (editMode) HudElementManager.setDescription("");
        }
    }

    @Override
    public boolean onMouseScrolled(double x, double y, double verticalAmount) {
        if (editMode) {
            HudComponent hoveredChild = getHoveredChild(x, y);
            if (hoveredChild != null) {
                return hoveredChild.onMouseScrolled(x, y, verticalAmount);
            } else {
                increaseScale(verticalAmount);
                return true;
            }
        } else if (this != HudElementManager.INSTANCE) {
            increaseScale(verticalAmount);
            return true;
        }
        return false;
    }
}
