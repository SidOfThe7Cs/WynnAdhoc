package sidly.wynnadhoc.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.MouseInput;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

public class MouseMoveEvent extends Event<MouseMoveEvent> {
    public Vector2d oldPos;
    public Vector2d newPos;
    public Vector2d oldPosScaled = new Vector2d();
    public Vector2d newPosScaled = new Vector2d();
    public MouseInput activeButton;

    public MouseMoveEvent(Vector2d oldPos, Vector2d newPos, MouseInput activeButton) {
        this.oldPos = oldPos;
        this.newPos = newPos;
        this.activeButton = activeButton;
        double scaleFactor = MinecraftClient.getInstance().getWindow().getScaleFactor();
        oldPos.div(scaleFactor, oldPosScaled);
        newPos.div(scaleFactor, newPosScaled);
        this.fire();
    }

    public boolean isLeftHeld() {
        return activeButton != null && activeButton.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT;
    }
}
