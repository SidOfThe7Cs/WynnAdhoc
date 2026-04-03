package sidly.wynnadhoc.utils;

import net.minecraft.client.MinecraftClient;
import org.joml.Vector2d;

public class GuiUtils {
    public static Vector2d getScaledMousePos() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.mouse == null) return new Vector2d(-1, -1);
        double mouseX = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
        double mouseY = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();
        return new Vector2d(mouseX, mouseY);
    }

    public static void setMouseGrabbed(boolean grabbed) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (grabbed && MinecraftClient.getInstance().currentScreen == null) client.mouse.lockCursor();
        else client.mouse.unlockCursor();
    }
}
