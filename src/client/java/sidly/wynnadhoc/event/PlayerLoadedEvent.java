package sidly.wynnadhoc.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class PlayerLoadedEvent extends Event<PlayerLoadedEvent> {
    private static boolean existedLastTick = false;
    public ClientPlayerEntity player;

    public PlayerLoadedEvent(ClientPlayerEntity player) {
        this.player = player;
        this.fire();
    }

    public static void onTick(ClientTickEvent event) {
        ClientPlayerEntity clientPlayer = MinecraftClient.getInstance().player;
        if (clientPlayer == null) {
            existedLastTick = false;
        } else if (!existedLastTick) {
            existedLastTick = true;
            new PlayerLoadedEvent(clientPlayer);
        }
    }
}
