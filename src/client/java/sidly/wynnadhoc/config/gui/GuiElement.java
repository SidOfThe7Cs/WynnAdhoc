package sidly.wynnadhoc.config.gui;

import net.minecraft.client.gui.DrawContext;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiElement extends HudComponent {
    // TODO this should not extend hubComponents nor should viewport extend data each element should have one viweport
    private final List<HudComponent> children = new ArrayList<>();

    public GuiElement(SubViewPort viewPort, HudComponent... children) {
        super(viewPort);
        this.children.addAll(Arrays.asList(children));
    }

    @Override
    public void render(Vector2i pos, DrawContext drawContext, boolean override) {
        super.renderBackground(drawContext);
        if (data() instanceof SubViewPort viewPort) {
            children.forEach(child -> child.render(viewPort, drawContext, override));
        }
        super.renderHover(drawContext);
    }

    @Override
    void updateDisplay() {

    }

    @Override
    boolean isVisible() {
        return false;
    }
}
