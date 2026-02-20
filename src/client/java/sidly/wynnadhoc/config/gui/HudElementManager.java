package sidly.wynnadhoc.config.gui;

import sidly.wynnadhoc.event.GuiRenderOnTopEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HudElementManager {
    private static final Map<String, HudElement> hudElements = new HashMap<>();

    public static Set<HudElement> getHudElements() {
        return new HashSet<>(hudElements.values());
    }

    public static HudElement getHudElement(String name) {
        return hudElements.get(name);
    }

    public static void register(HudElement e) {
        hudElements.put(e.name(), e);
    }

    public static void onHudRender(GuiRenderOnTopEvent event) {
        for (HudElement e : getHudElements()) {
            e.render(event.context, false);
        }
    }
}
