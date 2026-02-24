package sidly.wynnadhoc.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

public class WorldChangeEvent extends Event<WorldChangeEvent> {
    public MinecraftClient client;
    public ClientWorld world;

    public WorldChangeEvent(MinecraftClient client, ClientWorld world) {
        this.client = client;
        this.world = world;
        this.fire();
    }
}
