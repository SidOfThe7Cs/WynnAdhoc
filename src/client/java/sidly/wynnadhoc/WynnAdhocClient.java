package sidly.wynnadhoc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.gui.DraggableHudElementScreen;
import sidly.wynnadhoc.config.gui.HudElementManager;
import sidly.wynnadhoc.event.*;
import sidly.wynnadhoc.features.chests.AutoLootChests;
import sidly.wynnadhoc.features.chests.ChestTracker;
import sidly.wynnadhoc.features.lootruns.Overlays;
import sidly.wynnadhoc.features.outervoid.OuterVoidItemPathfinder;
import sidly.wynnadhoc.features.lootruns.ScoreboardInfo;
import sidly.wynnadhoc.utils.render.RenderUtils;
import sidly.wynnadhoc.features.war.DB;
import sidly.wynnadhoc.features.war.WarTimer;

public class WynnAdhocClient implements ClientModInitializer {
    public static final String MOD_ID = "wynnadhoc";

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.INSTANCE.save());
        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager.INSTANCE::save));

        // register events
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvent::onClientTick);
        WorldRenderEvents.AFTER_ENTITIES.register(ScreenCloseEvent::onFrameRendered);
        ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register(BlockEntityLoadedEvent::new);
        WorldRenderEvents.END_MAIN.register(RenderUtils.INSTANCE::onFabricWorldRender);

        Event.register(ClientTickEvent.class, ForEachEntityEvent::onClientTick);
        Event.register(ClientTickEvent.class, sidly.wynnadhoc.features.lootruns.Core.INSTANCE::onClientTick);
        Event.register(ClientTickEvent.class, ScoreboardInfo::parseScoreboard);
        Event.register(ClientTickEvent.class, OuterVoidItemPathfinder.INSTANCE::onClientTick);
        Event.register(ClientTickEvent.class, WarTimer::onClientTick);

        Event.register(InitEvent.class, OuterVoidItemPathfinder.INSTANCE::loadIslandNodes);

        Event.register(ChatMessageEvent.class, WarTimer::onChatMessage);
        Event.register(ChatMessageEvent.class, sidly.wynnadhoc.features.lootruns.Core.INSTANCE::onChatMessage);
        Event.register(ChatMessageEvent.class, sidly.wynnadhoc.features.war.Core::onChatMessage);

        Event.register(GuiRenderOnTopEvent.class, HudElementManager::onHudRender);

        Event.register(ScreenRenderEvent.class, DB::parseScreen);
        Event.register(ScreenRenderEvent.class, ChestItemsLoadedEvent::onScreenRender);
        Event.register(ScreenRenderEvent.class, sidly.wynnadhoc.features.lootruns.Core.INSTANCE::onScreenRender);

        Event.register(ScreenOpenedEvent.class, ChestItemsLoadedEvent::onScreenOpened);
        Event.register(ScreenOpenedEvent.class, sidly.wynnadhoc.features.lootruns.Core.INSTANCE::onScreenOpened);

        Event.register(ChestItemsLoadedEvent.class, ChestTracker::onChestItemsLoaded);
        Event.register(ChestItemsLoadedEvent.class, AutoLootChests::onChestItemsLoaded);

        Event.register(WorldRenderEvent.class, OuterVoidItemPathfinder.INSTANCE::draw);
        Event.register(WorldRenderEvent.class, sidly.wynnadhoc.features.lootruns.Core.INSTANCE::onWorldRender);

        Event.register(PreInitEvent.class, sidly.wynnadhoc.features.war.Core::registerHudElements);
        Event.register(PreInitEvent.class, Overlays::register);

        Event.register(BlockEntityLoadedEvent.class, sidly.wynnadhoc.features.lootruns.Core.INSTANCE::onBlockEntityLoad);
        Event.register(ForEachEntityEvent.class, sidly.wynnadhoc.features.lootruns.Core.INSTANCE::checkIfBeacon);
        Event.register(KeyboardEvent.class, DraggableHudElementScreen::onKeyPressed);

        ConfigManager.INSTANCE.load();

        new PreInitEvent();
        new InitEvent();
    }
}


/*TODO
on hover / click for war res display
save and load lootrun data
add all features with config
check all mixins and config options in wynntools
refactor to more "model" format
split mod
 */