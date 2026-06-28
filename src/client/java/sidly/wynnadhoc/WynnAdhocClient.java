package sidly.wynnadhoc;

import com.wynntils.mc.event.ChangeCarriedItemEvent;
import com.wynntils.mc.event.SetSlotEvent;
import com.wynntils.models.spells.event.SpellEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.gui.DraggableHudElementScreen;
import sidly.wynnadhoc.config.gui.HudElementManager;
import sidly.wynnadhoc.event.*;
import sidly.wynnadhoc.features.HealthRegenTick;
import sidly.wynnadhoc.features.ReParty;
import sidly.wynnadhoc.features.SpellMacros;
import sidly.wynnadhoc.features.WindPrison;
import sidly.wynnadhoc.features.chests.ChestTracker;
import sidly.wynnadhoc.features.guild.GuildLogs;
import sidly.wynnadhoc.features.item_tooltip.ItemTooltip;
import sidly.wynnadhoc.features.lootruns.LootrunCore;
import sidly.wynnadhoc.features.lootruns.LootrunLogger;
import sidly.wynnadhoc.features.lootruns.Overlays;
import sidly.wynnadhoc.features.lootruns.ScoreboardInfo;
import sidly.wynnadhoc.features.outervoid.OuterVoidItemDatabase;
import sidly.wynnadhoc.features.prof.ProfNodeCore;
import sidly.wynnadhoc.features.war.DB;
import sidly.wynnadhoc.features.war.WarCore;
import sidly.wynnadhoc.features.war.WarTimer;
import sidly.wynnadhoc.server.ChestCrowdsource;
import sidly.wynnadhoc.server.CrowdsourceMain;
import sidly.wynnadhoc.utils.Debug;
import sidly.wynnadhoc.utils.TickScheduler;
import sidly.wynnadhoc.utils.VersionUtils;
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
        AttackEntityCallback.EVENT.register(EntityClickedEvent::onEntityClicked);
        ClientCommandRegistrationCallback.EVENT.register(CommandRegistrationEvent::new);
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register(WorldChangeEvent::new);
        ScreenEvents.AFTER_INIT.register(ScreenOpenedEvent::new);
        ClientPlayConnectionEvents.DISCONNECT.register(ChestCrowdsource::submitChests);

        Event.register(ClientTickEvent.class, ForEachEntityEvent::onClientTick);
        Event.register(ClientTickEvent.class, LootrunCore.INSTANCE::onClientTick);
        Event.register(ClientTickEvent.class, ScoreboardInfo::parseScoreboard);
        Event.register(ClientTickEvent.class, WarTimer::onClientTick);
        Event.register(ClientTickEvent.class, HealthRegenTick::onTick);
        Event.register(ClientTickEvent.class, TickScheduler::tickAll);
        Event.register(ClientTickEvent.class, SpellMacros::onTick);
        Event.register(ClientTickEvent.class, PlayerLoadedEvent::onTick);

        Event.register(InitEvent.class, OuterVoidItemDatabase::init);

        Event.register(ChatMessageEvent.class, WarTimer::onChatMessage);
        Event.register(ChatMessageEvent.class, LootrunCore.INSTANCE::onChatMessage);
        Event.register(ChatMessageEvent.class, WarCore::onChatMessage);
        Event.register(ChatMessageEvent.class, GuildLogs.INSTANCE::onChatMessage);
        Event.register(ChatMessageEvent.class, ReParty::onChat);

        Event.register(HudRenderOnTopEvent.class, HudElementManager::onHudRender);

        Event.register(ScreenRenderEvent.class, DB::parseScreen);
        Event.register(ScreenRenderEvent.class, ChestItemsLoadedEvent::onScreenRender);
        Event.register(ScreenRenderEvent.class, LootrunCore.INSTANCE::onScreenRender);
        Event.register(ScreenRenderEvent.class, GuildLogs.INSTANCE::onScreenRender);

        Event.register(ScreenOpenedEvent.class, ChestItemsLoadedEvent::onScreenOpened);

        Event.register(ChestItemsLoadedEvent.class, ChestTracker.INSTANCE::onChestItemsLoaded);

        Event.register(WorldRenderEvent.class, ChestTracker.INSTANCE::onWorldRender);
        Event.register(WorldRenderEvent.class, ProfNodeCore::onRender);
        Event.register(WorldRenderEvent.class, ForEachEntityRenderEvent::onRender);

        Event.register(PreInitEvent.class, WarCore::registerHudElements);
        Event.register(PreInitEvent.class, Overlays::register);
        Event.register(PreInitEvent.class, GuildLogs.INSTANCE::registerHudElements);
        Event.register(PreInitEvent.class, SpellMacros::register);

        Event.register(ForEachEntityEvent.class, LootrunCore.INSTANCE::checkIfBeacon);
        Event.register(ForEachEntityEvent.class, NewItemDisplayEvent::onEachEntity);

        Event.register(CharacterUuidUpdateEvent.class, Overlays::updateAll);

        Event.register(KeyboardEvent.class, DraggableHudElementScreen::onKeyPressed);
        Event.register(MouseButtonEvent.class, HudElementManager::onMouseEvent);
        Event.register(MouseButtonEvent.class, SpellMacros::onMouseButton);
        Event.register(KeyboardEvent.class, SpellMacros::onKeyPressed);
        Event.register(SlotClickedEvent.class, LootrunCore.INSTANCE::onSlotClicked);
        Event.register(WorldChangeEvent.class, HealthRegenTick::onWorldChange);
        Event.register(WorldChangeEvent.class, SpellMacros::onWorldChange);
        Event.register(TextDisplaySyncEvent.class, ChestTracker.INSTANCE::onTextDisplaySync);
        Event.register(TextDisplaySyncEvent.class, ProfNodeCore::onTextDisplaySync);
        Event.register(EntityClickedEvent.class, ChestTracker.INSTANCE::onEntityClicked);
        Event.register(BlockEntityLoadedEvent.class, ChestTracker.INSTANCE::onBlockEntityLoad);
        Event.register(ForEachEntityRenderEvent.class, WindPrison::onEntity);
        Event.register(DrawTooltipEvent.class, ItemTooltip::onTooltipDraw);
        Event.register(PlayerLoadedEvent.class, CrowdsourceMain::startAuth);
        Event.register(PlayerLoadedEvent.class, VersionUtils::onPLayerLoad);

        Event.register(CommandRegistrationEvent.class, ChestCrowdsource::register);
        Event.register(CommandRegistrationEvent.class, CrowdsourceMain::registerCommands);
        Event.register(CommandRegistrationEvent.class, ReParty::registerCommands);

        NeoEvent.register(SpellEvent.Partial.class, SpellMacros::onPartial);
        NeoEvent.register(SpellEvent.Cast.class, SpellMacros::onCastEvent);
        NeoEvent.register(SpellEvent.Failed.class, SpellMacros::onFail);
        NeoEvent.register(ChangeCarriedItemEvent.class, SpellMacros::onItemSwap);
        NeoEvent.register(SetSlotEvent.Post.class, SpellMacros::onSetSlotEvent);

        LootrunLogger.load();
        ConfigManager.INSTANCE.load();

        new PreInitEvent();
        new InitEvent();
    }
}


/*TODO main list
move renderutils into worldrenderevent where possible (it should also be kotlin with optional args)

auto update checker
remove wynntills as depend and add function for hasWynntils and isOnWynncraft
icon
fix spellcaster
implement anchor points + layering
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