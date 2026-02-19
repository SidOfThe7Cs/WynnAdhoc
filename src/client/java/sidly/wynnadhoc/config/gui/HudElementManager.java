package sidly.wynnadhoc.config.gui;

import net.minecraft.client.MinecraftClient;
import sidly.wynnadhoc.event.ScreenRenderEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HudElementManager {
    private static final Map<String, HudElement> hudElements = new HashMap<>();

    public static Set<HudElement> getHudElements() {
        return new HashSet<>(hudElements.values());
    }

    public static void register(HudElement e) {
        hudElements.put(e.name(), e);
    }

    public static void onScreenRender(ScreenRenderEvent event) {
        for (HudElement e : getHudElements()) {
            e.render(event.context, false);
        }
    }

    public static void onHudRender() {
        if (MinecraftClient.getInstance().currentScreen != null) return; // draw on screen instead
        for (HudElement e : getHudElements()) {
            // TODO this basically needs a screen DrawContext even when screen is null
        }
    }
}
