package sidly.wynnadhoc.config.gui;

import net.minecraft.client.gui.DrawContext;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;

public class GuiElement extends HudComponent {
    // TODO this should not extend hubComponents nor should viewport extend data each element should have one viweport
    private final Map<String, HudComponent> children = new HashMap<>();

    public GuiElement(SubViewPort viewPort, HudComponent... children) {
        super(viewPort);
        for (HudComponent child : children) {
            child.setViewPort(viewPort);
            this.children.put(child.name(), child);
        }
    }

    @Override
    public void render(Vector2i pos, DrawContext drawContext, boolean override) {
        super.renderBackground(drawContext);
        children.values().forEach(child -> child.render(drawContext, override));
        super.renderHover(drawContext);
    }

    @Override
    void updateDisplay() {

    }

    @Override
    boolean isVisible() {
        return true;
    }
}
