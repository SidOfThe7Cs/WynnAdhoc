package sidly.wynnadhoc.features.raids

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.event.WorldRenderEvent
import sidly.wynnadhoc.utils.LocationUtils
import sidly.wynnadhoc.utils.datatypes.toBox
import sidly.wynnadhoc.utils.render.drawBox
import java.awt.Color
import kotlin.math.max
import kotlin.math.min


object NOL {
    fun onWorldRender(event: WorldRenderEvent) {
        if (!ConfigManager.INSTANCE.config.raid.showMazePath) return
        val client = MinecraftClient.getInstance()
        val playerPos = client.player?.entityPos ?: return
        // TODO actual raid coords
        if (LocationUtils.RaidRoom.getRaidRoom(playerPos) != LocationUtils.RaidRoom.NOL_THIRD_MAZE) return
        val world = client.world ?: return

        val corner1 = BlockPos(11776, 64, 2496)
        val corner2 = BlockPos(11855, 64, 2559)

        val minX = min(corner1.x, corner2.x)
        val maxX = max(corner1.x, corner2.x)
        val minZ = min(corner1.z, corner2.z)
        val maxZ = max(corner1.z, corner2.z)

        for (x in minX..maxX) {
            for (z in minZ..maxZ) {
                val pos = BlockPos(x, 64, z)
                val block: Block = world.getBlockState(pos).block

                if (Blocks.BARRIER == block) {
                    event.drawBox(pos.toBox(), Color.GREEN, solid = true)
                }
            }
        }
    }
}