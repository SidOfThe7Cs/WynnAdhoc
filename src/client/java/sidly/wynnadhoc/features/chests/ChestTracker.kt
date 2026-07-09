package sidly.wynnadhoc.features.chests

import com.wynntils.core.components.Models
import com.wynntils.models.containers.containers.reward.LootChestContainer
import io.github.notenoughupdates.moulconfig.ChromaColour.Companion.forLegacyString
import net.minecraft.block.Blocks
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.decoration.DisplayEntity
import net.minecraft.entity.decoration.InteractionEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import sidly.wynnadhoc.WynnAdhocClient
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.config.saves.ChestsSaveData
import sidly.wynnadhoc.event.*
import sidly.wynnadhoc.features.lootruns.LootrunCore
import sidly.wynnadhoc.features.lootruns.ScoreboardInfo
import sidly.wynnadhoc.features.lootruns.enums.MissionOptions
import sidly.wynnadhoc.server.ChestCrowdsource
import sidly.wynnadhoc.utils.Debug
import sidly.wynnadhoc.utils.LocationUtils
import sidly.wynnadhoc.utils.datatypes.toBox
import sidly.wynnadhoc.utils.render.drawBox
import kotlin.math.pow

object ChestTracker {
    val TIMER_REGEX = Regex("§7(?<timeLeft>\\d+)(?<unit>[dhms])")

    private val config get() = ConfigManager.INSTANCE.config.chest
    private var lastClickedChest: BlockPos? = null
    private val trappedChests = mutableSetOf<BlockPos>()
    private val chestDataCache: MutableMap<BlockPos, ChestDataCache> = mutableMapOf()

    fun getChestDataCache(): Map<BlockPos, ChestDataCache> {
        return chestDataCache
    }

    fun cacheChestData(global: Collection<LootChest>) {
        val local = ConfigManager.INSTANCE.chests
        val globalKeys = mutableSetOf<BlockPos>()
        // cache contains both local and global separately first we loop through all existing global and add both local and global data
        for (chest in global) {
            val pos = chest.pos ?: continue
            globalKeys.add(pos)
            chestDataCache[pos] = ChestDataCache.from(local[pos], chest)
        }
        // then we loop through remaining local and add with no global data
        for (entry in local) {
            if (globalKeys.contains(entry.key)) continue
            chestDataCache[entry.key] = ChestDataCache.from(entry.value, null)
        }
    }

    fun onBlockEntityLoad(event: BlockEntityLoadedEvent) {
        if (event.blockEntity is ChestBlockEntity) {
            val pos = event.blockEntity.getPos() ?: return
            val block = event.clientWorld.getBlockState(pos).block ?: return

            if (block === Blocks.TRAPPED_CHEST) {
                trappedChests.add(pos)
            }
        }
    }

    fun onEntityClicked(event: EntityClickedEvent) {
        if (event.entity is InteractionEntity) {
            lastClickedChest = null
            val pos = event.entity.entityPos.add(0.0, 1.27, 0.0)
            val searchBox = Box(pos.x - 0.7, pos.y - 0.7, pos.z - 0.7, pos.x + 0.7, pos.y + 0.7, pos.z + 0.7)
            val textDisplays = event.world.getEntitiesByClass(
                DisplayEntity.TextDisplayEntity::class.java,
                searchBox
            ) { _ -> true }

            if (textDisplays.any { t -> t.text.string.contains("Loot Chest") }) lastClickedChest =
                BlockPos.ofFloored(event.entity.entityPos)
        }
    }

    fun onChestItemsLoaded(event: ChestItemsLoadedEvent) {
        if (Models.Container.currentContainer is LootChestContainer && config.trackChests) {
            if (lastClickedChest == null) {
                WynnAdhocClient.LOGGER.warn("last clicked chest was null on chest items loaded")
                return
            }

            val chest = ConfigManager.INSTANCE.chests[lastClickedChest] ?: return
            chest.onOpen()
            ChestCrowdsource.changedKeys.add(lastClickedChest)

            val items = event.items.mapNotNull { i -> EncodableItem.fromItem(i) }
            WynnAdhocClient.LOGGER.info(Debug.Type.LOOTRUN, "found items: $items")
            val byteArray = EncodableItem.toByteArray(items)

            val newTotalItems = chest.addItems(byteArray)
            if (config.chestDisplayOptions.contains(ChestDataDisplayOption.LOCAL_ITEM_PERCENTS) ||
                config.chestDisplayOptions.contains(ChestDataDisplayOption.LOCAL_ING_PERCENTS)
            ) {
                chestDataCache[lastClickedChest]?.updateLocal(newTotalItems)
            } else chestDataCache[lastClickedChest]?.addLocalCounts(byteArray)
        }
    }

    fun onWorldRender(event: WorldRenderEvent) {
        val client = MinecraftClient.getInstance()
        val player = client.player
        if (client == null || player == null) return

        val crono = LootrunCore.isMissionActive(MissionOptions.Chronokinesis)
        val missionReq = ScoreboardInfo.missionChestReq > ScoreboardInfo.missionChestCurrent
        val splunk = ScoreboardInfo.splunkChestReq > ScoreboardInfo.splunkChestCurrent
        val highlightRelevantChests = ConfigManager.INSTANCE.config.lootrun.highlightRelevantChests
        val shouldHighLight = highlightRelevantChests && (missionReq || crono || splunk)
        if (shouldHighLight || config.forceEsp) {
            val currentTime = System.currentTimeMillis()
            val onlySplunk = splunk && !missionReq && !crono && !config.forceEsp
            for (knownChest in ConfigManager.INSTANCE.chests.entries) {
                if (!config.shownTiers.contains(ChestTier.from(knownChest.value.tier))) continue
                val isSplunkChest = knownChest.value.tier > 2 && splunk
                if (onlySplunk && !isSplunkChest) continue
                if (!isSplunkChest && config.onlyOpenable && !knownChest.value.isOpenable(currentTime)) continue
                val color = if (isSplunkChest) {
                    forLegacyString(config.readyColor).getEffectiveColour()
                } else knownChest.value.getColor(currentTime)

                val distSqr = knownChest.key.getSquaredDistance(player.entityPos)
                val maxDist = config.maxEspDistance.pow(2)
                if (distSqr > maxDist) continue

                event.drawBox(knownChest.key.toBox(), color)
            }
        }
    }

    fun onTextDisplaySync(event: TextDisplaySyncEvent) {
        if (event.string.contains("Loot Chest")) {
            val tier = when {
                event.string.contains("§7Loot Chest §7[§f✫§8✫✫✫§7]") -> 1
                event.string.contains("§eLoot Chest §e[§6✫✫§8✫✫§e]") -> 2
                event.string.contains("§5Loot Chest §5[§d✫✫✫§8✫§5]") -> 3
                event.string.contains("§3Loot Chest §3[§b✫✫✫✫§3]") -> 4
                else -> -1
            }
            if (tier == -1) WynnAdhocClient.LOGGER.info(
                Debug.Type.LOOTRUN,
                "found unknown lootchest tier at ${event.blockPos}: ${event.string}"
            )

            val blockPos: BlockPos = event.blockPos.down(1)

            if (!ConfigManager.INSTANCE.chests.containsKey(blockPos)) {
                ConfigManager.INSTANCE.chests[blockPos] = ChestsSaveData.ChestData(tier, ByteArray(0))
                chestDataCache[blockPos] = ChestDataCache.from(null, null)
            }

            if (config.displayLevel) addLevelRanges(event)
        }
        if (config.displayLevel && event.string.matches(TIMER_REGEX)) addLevelRanges(event)
    }

    fun addLevelRanges(event: TextDisplaySyncEvent) {
        val copy = event.text.copy()
        val chestPos = LocationUtils.getBlockUnderVec3d(event.pos)
        val chestDataCache = chestDataCache[chestPos] ?: return

        config.chestDisplayOptions.forEach { option ->
            val text = option.toText(chestDataCache)
            copy.siblings.addAll(text)
        }

        MinecraftClient.getInstance().execute {
            event.textDisplay.text = copy
        }
    }
}
