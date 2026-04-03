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
    private static final SubViewPort screenView = new SubViewPort("full screen", 0, 0, 1, 1, null);
    public static final GuiElement INSTANCE = new GuiElement(screenView);

    private static final Map<Integer, HudComponent> hudElementsMap = new HashMap<>(); // allows getting the component from just its data

    private static String description = "";
    private static HudComponent dragging = null;

    public static void setDescription(String description) {
        HudElementManager.description = description;
    }

    public static void setDragging(HudComponent dragging) {
        HudElementManager.dragging = dragging;
    }

    public static HudComponent getHudElement(HudComponentData data) {
        return hudElementsMap.get(System.identityHashCode(data));
    }

    public static void register(HudComponent e) {
        INSTANCE.addChild(e);
        hudElementsMap.put(System.identityHashCode(e.data()), e);
    }

    public static void onHudRender(HudRenderOnTopEvent event) {
        INSTANCE.render(event.context, false, 1f);
        if (!description.isEmpty()) {
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
        if (!event.isLeftClick()) return;
        if (event.isRelease()) setDragging(null);
        if (!event.isPress()) return;

        HudComponent hoveredChild = INSTANCE.getHoveredChild(event.pos.x, event.pos.y);
        if (hoveredChild != null) {
            hoveredChild.onMouseClicked(event.asClick(), false, INSTANCE.isEditMode());
        }
    }

    public static void onMouseScrollEvent(MouseScrollEvent event) {
        INSTANCE.onMouseScrolled(event.pos.x, event.pos.y, event.amount);
    }

    public static void onMouseMoveEvent(MouseMoveEvent event) {
        INSTANCE.onMouseMoved(event);
        if (dragging != null) {
            dragging.moveTo(event);
        }
    }

    public static void onKeyboardEvent(KeyboardEvent event) {
        if (event.action == 1 && event.key == config().guiEditorKeybind) {
            boolean wasInEdit = INSTANCE.isEditMode();
            GuiUtils.setMouseGrabbed(wasInEdit);
            INSTANCE.setEditMode(!wasInEdit); // TODO this reverts when you click disable that or smth
        }
    }
}
