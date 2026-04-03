package sidly.wynnadhoc.event;

import net.minecraft.client.gui.Click;
import net.minecraft.client.input.MouseInput;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import sidly.wynnadhoc.utils.GuiUtils;

public class MouseButtonEvent extends Event<MouseButtonEvent> {
    public MouseInput input;
    public int action;
    public Vector2d pos;
    private boolean consumed = false;

    public MouseButtonEvent(MouseInput input, int action) {
        this.input = input;
        this.action = action;
        this.pos = GuiUtils.getScaledMousePos();
        this.fire();
    }

    public void consume() {
        consumed = true;
    }

    public boolean consumed() {
        return consumed;
    }

    public boolean isRightClick() {
        return input.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
    }

    public boolean isLeftClick() {
        return input.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT;
    }

    public boolean isPress() {
        return action == GLFW.GLFW_PRESS;
    }

    public Click asClick() {
        return new Click(pos.x, pos.y, input);
    }

    public boolean isRelease() {
        return action == GLFW.GLFW_RELEASE;
    }
}
