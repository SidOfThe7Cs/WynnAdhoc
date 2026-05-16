package sidly.wynnadhoc.event;

import net.minecraft.client.input.MouseInput;
import org.lwjgl.glfw.GLFW;

public class MouseButtonEvent extends Event<MouseButtonEvent> {
    public MouseInput input;
    public int action;
    public boolean canceled = false;

    public MouseButtonEvent(MouseInput input, int action) {
        this.input = input;
        this.action = action;
        this.fire();
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
}
