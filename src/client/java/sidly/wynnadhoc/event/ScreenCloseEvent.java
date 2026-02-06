package sidly.wynnadhoc.event;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;

public class ScreenCloseEvent extends Event<ScreenCloseEvent> {
    private static boolean screenLastTick = false;

    public ScreenCloseEvent() {
        this.fire();
    }

    public static void onFrameRendered(WorldRenderContext worldRenderContext) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        if (client.currentScreen == null && screenLastTick) {
            new ScreenCloseEvent();
        }
        screenLastTick = client.currentScreen != null;
    }
}
