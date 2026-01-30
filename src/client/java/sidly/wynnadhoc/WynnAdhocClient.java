package sidly.wynnadhoc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import sidly.wynnadhoc.config.ConfigManager;

public class WynnAdhocClient implements ClientModInitializer {
    public static final String MOD_ID = "WynnAdhoc";

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.INSTANCE.save());
        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager.INSTANCE::save));

        ConfigManager.INSTANCE.load();
	}
}

/*TODO
fire events
test rendering
use moul hud elements
add all features with config
 */