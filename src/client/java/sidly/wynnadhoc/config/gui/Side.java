package sidly.wynnadhoc.config.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import sidly.wynnadhoc.event.MouseMoveEvent;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static sidly.wynnadhoc.config.gui.HudComponent.TOLERANCE;

public enum Side {
    TOP(GLFW.GLFW_RESIZE_NS_CURSOR),
    LEFT(GLFW.GLFW_RESIZE_EW_CURSOR),
    BOTTOM(GLFW.GLFW_RESIZE_NS_CURSOR),
    RIGHT(GLFW.GLFW_RESIZE_EW_CURSOR),
    TOP_LEFT(GLFW.GLFW_RESIZE_NESW_CURSOR),
    TOP_RIGHT(GLFW.GLFW_RESIZE_NWSE_CURSOR),
    BOTTOM_LEFT(GLFW.GLFW_RESIZE_NWSE_CURSOR),
    BOTTOM_RIGHT(GLFW.GLFW_RESIZE_NESW_CURSOR),
    ALL(GLFW.GLFW_RESIZE_ALL_CURSOR),
    NONE(GLFW.GLFW_ARROW_CURSOR);

    private final int cursor;

    Side(int cursor) {
        this.cursor = cursor;
    }

    public void setCursor() {
        long window = MinecraftClient.getInstance().getWindow().getHandle();
        long cursor = GLFW.glfwCreateStandardCursor(this.cursor);
        GLFW.glfwSetCursor(window, cursor);
        if (this != NONE) HudElementManager.cursorChanged = true;
    }

    public static Side from(HudComponent hud, MouseMoveEvent event) {
        Vector2f p = hud.getScaledRenderPos();
        return Side.from(
                (int) p.x,
                (int) (p.x + hud.getScaledWidth()),
                (int) p.y,
                (int) (p.y + hud.getScaledHeight()),
                new Click(event.newPosScaled.x, event.newPosScaled.y, event.activeButton),
                TOLERANCE
        );
    }

    public static Side from(int xSmall, int xLarge, int ySmall, int yLarge, Click click, int tolerance) {
        Map<Side, Integer> map = new HashMap<>();

        map.put(TOP, Math.abs((int) click.y() - ySmall));
        map.put(BOTTOM, Math.abs((int) click.y() - yLarge));
        map.put(LEFT, Math.abs((int) click.x() - xSmall));
        map.put(RIGHT, Math.abs((int) click.x() - xLarge));

        List<Side> list = map.entrySet().stream()
                .filter((e) -> e.getValue() <= tolerance)
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .toList();

        if (list.isEmpty()) return NONE;
        else if (list.size() == 1) return list.getFirst();
        else {
            if (list.contains(TOP)) {
                if (list.contains(LEFT)) return TOP_LEFT;
                else if (list.contains(RIGHT)) return TOP_RIGHT;
            } else if (list.contains(BOTTOM)) {
                if (list.contains(LEFT)) return BOTTOM_LEFT;
                else if (list.contains(RIGHT)) return BOTTOM_RIGHT;
            }
        }
        return NONE;
    }
}
