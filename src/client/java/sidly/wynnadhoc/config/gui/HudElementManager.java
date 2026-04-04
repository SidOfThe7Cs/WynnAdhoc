package sidly.wynnadhoc.config.gui;

import net.minecraft.client.MinecraftClient;
import org.joml.Vector2d;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.GuiConfig;
import sidly.wynnadhoc.event.*;
import sidly.wynnadhoc.utils.GuiUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HudElementManager {
    private static GuiConfig config() {
        return ConfigManager.INSTANCE.config.gui;
    }

    // the main viewport that contains all other hud elements including viewports
    private static final HudComponentData screenView = new HudComponentData.Builder("full screen", 0, 0).width(1).height(1).build();
    public static final GuiElement INSTANCE = new GuiElement(screenView);

    private static final Map<Integer, HudComponent> hudElementsMap = new HashMap<>(); // allows getting the component from just its data

    private static String description = "";
    private static HudComponent dragging = null;
    private static Side expandingSide = Side.NONE;
    public static boolean cursorChanged = false;

    public static void setDescription(String description) {
        HudElementManager.description = description;
    }

    public static void setDragging(HudComponent dragging) {
        HudElementManager.dragging = dragging;
        expandingSide = Side.ALL;
    }

    public static boolean isDragging() {
        return dragging != null;
    }

    public static void setExpanding(HudComponent hudComponent, Side side) {
        dragging = hudComponent;
        expandingSide = side;
        if (side != Side.NONE) side.setCursor();
    }

    public static void resetCursor() {
        if (cursorChanged && dragging == null) {
            Side.NONE.setCursor();
            cursorChanged = false;
        }
    }

    public static HudComponent getHudElement(HudComponentData data) {
        return hudElementsMap.get(System.identityHashCode(data));
    }

    public static void register(HudComponent e) {
        INSTANCE.addChild(e);
        hudElementsMap.put(System.identityHashCode(e.data()), e);
    }

    public static void onHudRender(HudRenderOnTopEvent event) {
        INSTANCE.render(event.context, false);
        if (!description.isEmpty() && INSTANCE.isEditMode()) {
            Vector2d mousePos = GuiUtils.getScaledMousePos();
            event.context.drawText(
                    MinecraftClient.getInstance().textRenderer,
                    description,
                    (int) mousePos.x + 7,
                    (int) mousePos.y,
                    Color.white.getRGB(),
                    true
            );
        }
    }

    public static void onMouseButtonEvent(MouseButtonEvent event) {
        if (INSTANCE.isEditMode()) event.consume(); // consume cancels other minecraft handling but not mine
        if (event.isRelease()) setDragging(null);
        if (!event.isPress()) return;

        INSTANCE.onMouseClicked(event, false);
    }

    public static void onMouseScrollEvent(MouseScrollEvent event) {
        if (INSTANCE.onMouseScrolled(event.pos.x, event.pos.y, event.amount)) {
            event.consume();
        }
    }

    public static void onMouseMoveEvent(MouseMoveEvent event) {
        resetCursor();
        INSTANCE.onMouseMoved(event);
        if (dragging != null) {
            if (expandingSide != Side.ALL) {
                dragging.expandTo(expandingSide, event);
            } else dragging.moveTo(event);
        }
    }

    public static void onKeyboardEvent(KeyboardEvent event) {
        if (event.action == 1) {
            if (event.keyInput.key() == config().guiEditorKeybind) toggleEditor();
            else if (event.keyInput.isEscape()) closeEditor();
        }
    }

    public static void openEditor() {
        if (!INSTANCE.isEditMode()) INSTANCE.enableEditMode();
        GuiUtils.setMouseGrabbed(false);
    }

    public static void closeEditor() {
        if (INSTANCE.isEditMode()) INSTANCE.disableEditMode();
        GuiUtils.setMouseGrabbed(true);
    }

    public static void toggleEditor() {
        boolean wasInEdit = INSTANCE.isEditMode();
        GuiUtils.setMouseGrabbed(wasInEdit);
        INSTANCE.toggleEditMode();
    }
}
