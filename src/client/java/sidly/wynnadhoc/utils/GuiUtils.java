package sidly.wynnadhoc.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Pair;

public class GuiUtils {
    public static Pair<Double, Double> getScaledMousePos() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.mouse == null) return null;
        double mouseX = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
        double mouseY = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();
        return new Pair<>(mouseX, mouseY);
    }
}
