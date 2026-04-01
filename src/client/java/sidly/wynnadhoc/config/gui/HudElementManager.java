package sidly.wynnadhoc.config.gui;

import net.minecraft.client.gui.Click;
import net.minecraft.util.Pair;
import sidly.wynnadhoc.event.HudRenderOnTopEvent;
import sidly.wynnadhoc.event.MouseButtonEvent;
import sidly.wynnadhoc.utils.GuiUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HudElementManager {
    private static final Map<String, HudComponent> hudElements = new HashMap<>();

    public static Set<HudComponent> getHudElements() {
        return new HashSet<>(hudElements.values());
    }

    public static HudComponent getHudElement(String name) {
        return hudElements.get(name);
    }

    public static void register(HudComponent e) {
        hudElements.put(e.name(), e);
    }

    public static void onHudRender(HudRenderOnTopEvent event) {
        for (HudComponent e : getHudElements()) {
            e.render(event.context, false);
        }
    }

    public static void onMouseEvent(MouseButtonEvent event) {
        if (!event.isLeftClick()) return;
        if (!event.isPress()) return;

        Pair<Double, Double> mousePos = GuiUtils.getScaledMousePos();
        if (mousePos == null) return;

        for (HudComponent hudComponent : getHudElements()) {
            if (hudComponent != null && hudComponent.isVisible() && hudComponent.isHovering(mousePos.getLeft(), mousePos.getRight())) {
                Click click = new Click(mousePos.getLeft(), mousePos.getRight(), event.input);
                hudComponent.onMouseClicked(click, false);
            }
        }
    }
}
