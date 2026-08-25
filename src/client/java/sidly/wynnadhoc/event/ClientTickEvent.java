package sidly.wynnadhoc.event;

import net.minecraft.client.MinecraftClient;

public class ClientTickEvent extends Event<ClientTickEvent> {
    private static long counter = 0;

    public MinecraftClient client;

    public boolean onlyEveryX(int x) {
        return counter % x == 0;
    }

    public boolean onlyEveryXSeconds(int seconds) {
        return counter % (seconds * 20L) == 0;
    }

    public ClientTickEvent(MinecraftClient client) {
        this.client = client;
        if (client != null) {
            counter++;
            this.fire();
        }
    }

    public static void onClientTick(MinecraftClient client) {
        new ClientTickEvent(client);
    }
}
