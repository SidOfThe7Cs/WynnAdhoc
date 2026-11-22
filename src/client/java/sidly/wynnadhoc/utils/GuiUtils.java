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

    public static boolean isMouseWithinBound(double x1, double y1, double x2, double y2) {
        Pair<Double, Double> mouse = getScaledMousePos();
        if (mouse == null) return false;
        else if (mouse.getLeft() < x1) return false;
        else if (mouse.getLeft() > x2) return false;
        else if (mouse.getRight() < y1) return false;
        else return mouse.getRight() <= y2;
    }
}
