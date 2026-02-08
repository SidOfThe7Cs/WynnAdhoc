package sidly.wynnadhoc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.gui.DraggableHudElementScreen;
import sidly.wynnadhoc.config.gui.HudElementManager;
import sidly.wynnadhoc.config.gui.TextHudElement;
import sidly.wynnadhoc.event.*;
import sidly.wynnadhoc.lootruns.LootrunningUtils;
import sidly.wynnadhoc.lootruns.ScoreboardUtils;
import sidly.wynnadhoc.war.DB;
import sidly.wynnadhoc.war.WarTimer;
import sidly.wynnadhoc.war.WarUtils;

public class WynnAdhocClient implements ClientModInitializer {
    public static final String MOD_ID = "WynnAdhoc";

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.INSTANCE.save());
        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager.INSTANCE::save));

        // register events
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvent::onClientTick);
        WorldRenderEvents.AFTER_ENTITIES.register(ScreenCloseEvent::onFrameRendered);
        ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register(BlockEntityLoadedEvent::new);

        Event.register(ClientTickEvent.class, ForEachEntityEvent::onClientTick);
        Event.register(ClientTickEvent.class, LootrunningUtils::onClientTick);
        Event.register(ClientTickEvent.class, ScoreboardUtils::parseScoreboard);

        Event.register(ChatMessageEvent.class, WarTimer::onChatMessage);
        Event.register(ChatMessageEvent.class, LootrunningUtils::onChatMessage);
        Event.register(ChatMessageEvent.class, WarUtils::onChatMessage);

        Event.register(ScreenRenderEvent.class, DB::parseScreen);
        Event.register(ScreenRenderEvent.class, ChestItemsLoadedEvent::onScreenRender);
        Event.register(ScreenRenderEvent.class, LootrunningUtils::onScreenRender);
        Event.register(ScreenRenderEvent.class, HudElementManager::onScreenRender);

        Event.register(ScreenOpenedEvent.class, ChestItemsLoadedEvent::onScreenOpened);
        Event.register(ScreenOpenedEvent.class, LootrunningUtils::onScreenOpened);

        Event.register(BlockEntityLoadedEvent.class, LootrunningUtils::onBlockEntityLoad);
        Event.register(ForEachEntityEvent.class, LootrunningUtils::checkIfBeacon);
        Event.register(KeyboardEvent.class, DraggableHudElementScreen::onKeyPressed);

        ConfigManager.INSTANCE.load();

        // register hud elements (should really be done with an annotation)
        HudElementManager.register(new TextHudElement(
                ConfigManager.INSTANCE.config.war.getResourceOverlay(),
                WarUtils::shouldShowResourceOverlay,
                WarUtils::updateResourceDisplay,
                WarUtils::onWarResourceDisplayClick)
        );
    }
}

/*TODO
test rendering
on hover / click for war res display
save and load lootrun data
add all features with config
refactor to more "model" format
 */