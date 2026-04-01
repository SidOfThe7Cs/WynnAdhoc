package sidly.wynnadhoc.config.gui;

import net.minecraft.client.gui.DrawContext;
import org.joml.Vector2i;

public class MapHudComponent extends HudComponent {
    // TODO mayebe do texturehudelement first
    public MapHudComponent(HudComponentData data) {
        super(data);
    }

    @Override
    void render(Vector2i pos, DrawContext drawContext, boolean override) {

    }

    @Override
    void updateDisplay() {

    }

    @Override
    boolean isVisible() {
        return false;
    }
}
