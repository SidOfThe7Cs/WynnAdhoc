package sidly.wynnadhoc.utils.auto;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;
import java.util.Queue;

public class MouseLerper {
    private static class LerpTask {
        final double targetX, targetY;
        double pixelsPerTick;
        final Runnable onFinish;

        LerpTask(double targetX, double targetY, int pixelsPerTick, Runnable onFinish) {
            this.targetX = targetX;
            this.targetY = targetY;
            this.pixelsPerTick = pixelsPerTick;
            this.onFinish = onFinish;
        }
    }

    private static final Queue<LerpTask> queue = new LinkedList<>();
    private static boolean active = false;
    private static double startX, startY, targetX, targetY;
    private static int totalSteps, currentStep;
    private static long window;
    private static Runnable currentOnFinish;

    /**
     * Queues a new mouse lerp movement.
     * @param client Minecraft client instance
     * @param targetX target screen X coordinate
     * @param targetY target screen Y coordinate
     * @param pixelsPerTick number of pixels to move mouse per tick
     * @param onFinish callback to run after this lerp finishes (optional)
     */
    public static void queueLerp(MinecraftClient client, double targetX, double targetY, int pixelsPerTick, Runnable onFinish) {
        queue.add(new LerpTask(targetX, targetY, pixelsPerTick, onFinish));
        if (!active) startNext(client);
    }

    /**
     * Cancels all queued and active lerps immediately.
     */
    public static void cancelAll() {
        queue.clear();
        active = false;
        currentOnFinish = null;
    }

    /**
     * Should be called every client tick.
     */
    public static void tick() {
        if (!active) return;

        currentStep++;
        double progress = (double) currentStep / totalSteps;
        double newX = startX + (targetX - startX) * progress;
        double newY = startY + (targetY - startY) * progress;

        GLFW.glfwSetCursorPos(window, newX, newY);

        if (currentStep >= totalSteps) {
            active = false;

            // Run completion callback
            if (currentOnFinish != null) currentOnFinish.run();

            // Start next queued movement (if any)
            MinecraftClient client = MinecraftClient.getInstance();
            startNext(client);
        }
    }

    private static void startNext(MinecraftClient client) {
        LerpTask next = queue.poll();
        if (next == null) return;

        window = client.getWindow().getHandle();

        double[] curX = new double[1];
        double[] curY = new double[1];
        GLFW.glfwGetCursorPos(window, curX, curY);

        startX = curX[0];
        startY = curY[0];
        targetX = next.targetX;
        targetY = next.targetY;

        double dx = targetX - startX;
        double dy = targetY - startY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        totalSteps = (int)Math.max(1, Math.ceil(distance / next.pixelsPerTick));
        currentStep = 0;

        currentOnFinish = next.onFinish;
        active = true;
    }

    public static boolean isDone() {
        return !active && queue.isEmpty();
    }
}
