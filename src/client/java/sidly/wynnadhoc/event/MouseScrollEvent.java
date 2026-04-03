package sidly.wynnadhoc.event;

import org.joml.Vector2d;
import sidly.wynnadhoc.utils.GuiUtils;

public class MouseScrollEvent extends Event<MouseScrollEvent> {
    public double amount;
    public Vector2d pos;
    private boolean consumed = false;

    public MouseScrollEvent(double vertical) {
        amount = vertical;
        this.pos = GuiUtils.getScaledMousePos();
        this.fire();
    }

    public void consume() {
        consumed = true;
    }

    public boolean consumed() {
        return consumed;
    }
}
