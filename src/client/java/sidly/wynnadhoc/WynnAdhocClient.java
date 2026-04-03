package sidly.wynnadhoc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.gui.HudElementManager;
import sidly.wynnadhoc.event.*;
import sidly.wynnadhoc.features.HealthRegenTick;
import sidly.wynnadhoc.features.TradeMarketOverlay;
import sidly.wynnadhoc.features.chests.ChestTracker;
import sidly.wynnadhoc.features.guild.GuildLogs;
import sidly.wynnadhoc.features.lootruns.LootrunCore;
import sidly.wynnadhoc.features.lootruns.LootrunLogger;
import sidly.wynnadhoc.features.lootruns.Overlays;
import sidly.wynnadhoc.features.lootruns.ScoreboardInfo;
import sidly.wynnadhoc.features.outervoid.OuterVoidItemDatabase;
import sidly.wynnadhoc.features.war.DB;
import sidly.wynnadhoc.features.war.WarCore;
import sidly.wynnadhoc.features.war.WarTimer;
import sidly.wynnadhoc.utils.Debug;
import sidly.wynnadhoc.utils.TickScheduler;
import sidly.wynnadhoc.utils.render.RenderUtils;

public class WynnAdhocClient implements ClientModInitializer {
    public static final String MOD_ID = "wynnadhoc";
    public static final Debug LOGGER = new Debug(MOD_ID);

    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.INSTANCE.save());
        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager.INSTANCE::save));

        // register events
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvent::onClientTick);
        WorldRenderEvents.AFTER_ENTITIES.register(ScreenCloseEvent::onFrameRendered);
        ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register(BlockEntityLoadedEvent::new);
        WorldRenderEvents.END_MAIN.register(RenderUtils.INSTANCE::onFabricWorldRender);
        UseEntityCallback.EVENT.register(EntityClickedEvent::onEntityClicked);
        ClientCommandRegistrationCallback.EVENT.register(CommandRegistrationEvent::new);
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register(WorldChangeEvent::new);
        ScreenEvents.AFTER_INIT.register(ScreenOpenedEvent::new);

        Event.register(ClientTickEvent.class, ForEachEntityEvent::onClientTick);
        Event.register(ClientTickEvent.class, LootrunCore.INSTANCE::onClientTick);
        Event.register(ClientTickEvent.class, ScoreboardInfo::parseScoreboard);
        Event.register(ClientTickEvent.class, WarTimer::onClientTick);
        Event.register(ClientTickEvent.class, HealthRegenTick::onTick);
        Event.register(ClientTickEvent.class, TickScheduler::tickAll);

        Event.register(ChatMessageEvent.class, WarTimer::onChatMessage);
        Event.register(ChatMessageEvent.class, LootrunCore.INSTANCE::onChatMessage);
        Event.register(ChatMessageEvent.class, WarCore::onChatMessage);
        Event.register(ChatMessageEvent.class, GuildLogs.INSTANCE::onChatMessage);

        Event.register(HudRenderOnTopEvent.class, HudElementManager::onHudRender);

        Event.register(ScreenRenderEvent.class, DB::parseScreen);
        Event.register(ScreenRenderEvent.class, ChestItemsLoadedEvent::onScreenRender);
        Event.register(ScreenRenderEvent.class, LootrunCore.INSTANCE::onScreenRender);
        Event.register(ScreenRenderEvent.class, GuildLogs.INSTANCE::onScreenRender);

        Event.register(ScreenOpenedEvent.class, ChestItemsLoadedEvent::onScreenOpened);

        Event.register(ChestItemsLoadedEvent.class, ChestTracker.INSTANCE::onChestItemsLoaded);

        Event.register(WorldRenderEvent.class, ChestTracker.INSTANCE::onWorldRender);

        Event.register(PreInitEvent.class, WarCore::registerHudElements);
        Event.register(PreInitEvent.class, Overlays::register);
        Event.register(PreInitEvent.class, GuildLogs.INSTANCE::registerHudElements);
        Event.register(PreInitEvent.class, TradeMarketOverlay::registerHudElements);

        Event.register(InitEvent.class, OuterVoidItemDatabase::init);

        Event.register(ForEachEntityEvent.class, LootrunCore.INSTANCE::checkIfBeacon);
        Event.register(ForEachEntityEvent.class, NewItemDisplayEvent::onEachEntity);

        Event.register(MouseButtonEvent.class, HudElementManager::onMouseButtonEvent);
        Event.register(MouseScrollEvent.class, HudElementManager::onMouseScrollEvent);
        Event.register(MouseMoveEvent.class, HudElementManager::onMouseMoveEvent);
        Event.register(KeyboardEvent.class, HudElementManager::onKeyboardEvent);

        Event.register(SlotClickedEvent.class, LootrunCore.INSTANCE::onSlotClicked);
        Event.register(WorldChangeEvent.class, HealthRegenTick::onWorldChange);
        Event.register(TextDisplaySyncEvent.class, ChestTracker.INSTANCE::onTextDisplaySync);
        Event.register(EntityClickedEvent.class, ChestTracker.INSTANCE::onEntityClicked);
        Event.register(BlockEntityLoadedEvent.class, ChestTracker.INSTANCE::onBlockEntityLoad);

        LootrunLogger.load();
        ConfigManager.INSTANCE.load();

        new PreInitEvent();
        new InitEvent();
    }
}


/*TODO main list
fruma:
refactor chest saving to not just be there entir tooltip as json

auto update checker
icon

HUD:
    NON PIXEL VIEWPORTS - make viewports 2 corner and not pixel based? (remove subviewport merge it into data and then rename guielement to viewport)
    RESIZE VIEWPORTS - allow resizing viewports
    SUBELEMENTS - actually make way to enter edit mode recursivly
    ANCHOR - implement anchor points
        keybind when holding to change anchor point and swap to allow movement inside viewports
    RESORT - resort children anytime there size changes (maybe only when we open the editor)
    INFO TEXT - static element in center of screen with general info
    KEYBOARD - add keyboard controls (store last clicked add a boolean for dragging)
    FIX SCREEN EDGES: fix it not forcing hudelements on screen for viewports and bottom and right
    VISIBILITY CONDITION - every hudelement needs one not just text

remove wynntills as depend and add function for hasWynntils and isOnWynncraft (maybe a simpler way as well like an anotation or smth to make features wynn only)
spellcaster with queue and display and safe cast
annotation for hudelement and event

WAR:
    + res doesnt always display full/empty in (maybe not when onClick changes?)
    + resource tick display
    + notification when terr almost off cd
    + notification on buffed terr cut off
    + taxes and tributes support
    + add loadout support
    + check total guild cost / output diamond and compare
    + why does it sometime show less output than it should ie resource multiplier of 9.9 instead of 11
    + confirm queueing if difficulty or cost
 */