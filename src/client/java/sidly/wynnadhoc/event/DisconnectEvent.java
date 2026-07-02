package sidly.wynnadhoc.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public class DisconnectEvent extends Event<DisconnectEvent> {
    public DisconnectEvent() {
        this.fire();
    }

    public DisconnectEvent(ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient minecraftClient) {
        this();
    }
}
