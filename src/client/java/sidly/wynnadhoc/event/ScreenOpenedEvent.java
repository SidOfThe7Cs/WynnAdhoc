package sidly.wynnadhoc.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class ScreenOpenedEvent extends Event<ScreenOpenedEvent> {
    public MinecraftClient client;
    public Screen screen;
    public int width;
    public int height;

    public ScreenOpenedEvent(MinecraftClient client, Screen screen, int width, int height) {
        this.client = client;
        this.screen = screen;
        this.width = width;
        this.height = height;
        this.fire();
    }
}
