package sidly.wynnadhoc.event;

import net.minecraft.client.MinecraftClient;

public class ScreenCloseEvent extends Event<ScreenCloseEvent> {
    private static boolean screenLastTick = false;

    public static void onFrameRender() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        if (client.currentScreen == null && screenLastTick) {
            new ScreenCloseEvent().fire();
        }
        screenLastTick = client.currentScreen != null;
    }
}
