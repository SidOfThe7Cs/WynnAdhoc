package sidly.wynnadhoc.features.chests

import com.wynntils.core.components.Models
import com.wynntils.models.containers.containers.reward.LootChestContainer
import net.minecraft.block.Blocks
import net.minecraft.block.entity.ChestBlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.decoration.InteractionEntity
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import sidly.wynnadhoc.WynnAdhocClient
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.config.saves.ChestsSaveData
import sidly.wynnadhoc.event.*
import sidly.wynnadhoc.features.lootruns.LootrunCore.getCurrentLootrunData
import sidly.wynnadhoc.features.lootruns.ScoreboardInfo
import sidly.wynnadhoc.features.lootruns.enums.MissionOptions
import sidly.wynnadhoc.utils.Debug
import sidly.wynnadhoc.utils.LocationUtils
import sidly.wynnadhoc.utils.datatypes.LevelRange
import sidly.wynnadhoc.utils.datatypes.toBox
import sidly.wynnadhoc.utils.render.drawBox
import kotlin.math.pow

object ChestTracker {
    private val config get() = ConfigManager.INSTANCE.config.chest
    private var lastClickedChest: BlockPos? = null
    private val trappedChests = mutableSetOf<BlockPos>()

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
        if (event.hitResult.entity is InteractionEntity) {
            val pos = BlockPos.ofFloored(event.hitResult.entity.entityPos)
            val block = event.world.getBlockState(pos).block
            if (block === Blocks.CHEST || block === Blocks.TRAPPED_CHEST) {
                lastClickedChest = pos
            }
        }
    }

    fun onChestItemsLoaded(event: ChestItemsLoadedEvent) {
        if (Models.Container.currentContainer is LootChestContainer && config.trackChests) {
            if (lastClickedChest == null) {
                WynnAdhocClient.LOGGER.error("last clicked chest was null on chest items loaded")
                return
            }

            ConfigManager.INSTANCE.chests[lastClickedChest]?.onOpen()

            val items = event.getItems()
            val record = ChestRecord(lastClickedChest, items)

            ChestSaving.currentLoaded.add(record)
            ChestSaving.saveLatest()
        }
    }

    // TODO check tier and add selector to set tier to show only non caves during lootrun / forced
    fun onWorldRender(event: WorldRenderEvent) {
        val data = getCurrentLootrunData() ?: return
        val client = MinecraftClient.getInstance()
        val player = client.player
        if (client == null || player == null) return

        val crono = data.currentMissionsActive.contains(MissionOptions.Chronokinesis)
        val missionReq = ScoreboardInfo.missionChestReq > ScoreboardInfo.missionChestCurrent
        if (missionReq || crono || config.forceEsp) {
            val currentTime = System.currentTimeMillis()

            for (knownChest in ConfigManager.INSTANCE.chests.entries) {
                val color = knownChest.value.getColor(currentTime)

                val distSqr = knownChest.key.getSquaredDistance(player.entityPos)

                val maxDist = config.maxEspDistance.pow(2)
                if (distSqr > maxDist) continue

                if (config.shownColors.contains(ChestColor.from(color)) &&
                    config.shownTiers.contains(ChestTier.from(knownChest.value.tier))
                ) {
                    event.drawBox(knownChest.key.toBox(), color)
                }
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
                "found unknown lootchest tier at ${event.blockPos}:"
            )

            val world = MinecraftClient.getInstance().world ?: return
            val blockPos: BlockPos = event.blockPos.down(1)
            val block = world.getBlockEntity(blockPos) ?: return

            if (block is ChestBlockEntity) {
                if (!ConfigManager.INSTANCE.chests.containsKey(blockPos)) {
                    ConfigManager.INSTANCE.chests[blockPos] = ChestsSaveData.ChestData(tier)
                }
            }

            if (config.displayLevel) addLevelRanges(event)
        }
    }

    // TODO make draggable list with even more options like possible mythics
    fun addLevelRanges(event: TextDisplaySyncEvent) {
        val copy = event.text.copy()
        val chestPos = LocationUtils.getBlockUnderVec3d(event.pos)
        val allRecordsForChest = ChestSaving.getAllRecordsForChest(chestPos)
        val lvlQuantities: MutableMap<LevelRange, Int> = HashMap()
        for (record in allRecordsForChest) {
            val recordLvlQuantities = record.getLvlQuantities()
            recordLvlQuantities.forEach { (key: LevelRange, value: Int) ->
                lvlQuantities.merge(
                    key,
                    value
                ) { a: Int, b: Int -> Integer.sum(a, b) }
            }
        }
        val total = lvlQuantities.values.stream().mapToInt { obj: Int -> obj }.sum()
        copy.siblings.add(Text.of("\nBoxes Found: $total"))
        lvlQuantities.entries.stream()
            .sorted { a: MutableMap.MutableEntry<LevelRange, Int>, b: MutableMap.MutableEntry<LevelRange, Int> ->
                b.value.compareTo(a.value)
            }
            .forEach { entry: MutableMap.MutableEntry<LevelRange, Int> ->
                val percent = ((entry.value / total.toDouble()) * 100).toInt()
                copy.siblings.add(Text.of("\n" + entry.key + " " + percent + "%"))
            }

        event.textDisplay.text = copy
    }
}
