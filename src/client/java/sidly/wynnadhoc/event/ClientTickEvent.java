package sidly.wynnadhoc.event;

import net.minecraft.client.MinecraftClient;

public class ClientTickEvent extends Event<ClientTickEvent> {
    public MinecraftClient client;

    public ClientTickEvent(MinecraftClient client) {
        this.client = client;
        if (client != null) {
            this.fire();
        }
    }

    public static void onClientTick(MinecraftClient client) {
        new ClientTickEvent(client);
    }
}
