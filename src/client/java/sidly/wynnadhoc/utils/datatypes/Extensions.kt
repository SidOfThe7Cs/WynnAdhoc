package sidly.wynnadhoc.utils.datatypes

import net.minecraft.block.ShapeContext
import net.minecraft.client.MinecraftClient
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import net.minecraft.world.RaycastContext
import org.joml.Vector3f
import sidly.wynnadhoc.models.CameraModel
import sidly.wynnadhoc.utils.render.Line
import java.awt.Color

fun Vec3d.toBlockPos(): BlockPos {
    return BlockPos(this.x.toInt(), this.y.toInt(), this.z.toInt())
}

fun BlockPos.toBox(): Box {
    return Box(this)
}

fun Vec3i.toBox(): Box {
    return Box(BlockPos(this))
}

fun Vec3i.toVec3d(): Vec3d {
    return Vec3d(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
}

fun Vector3f.toVec3d(): Vec3d {
    return Vec3d(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
}

fun Vec3d.down(amount: Int): Vec3d {
    return Vec3d(this.x, this.y - amount, this.z)
}

fun Vec3d.up(amount: Int): Vec3d {
    return Vec3d(this.x, this.y + amount, this.z)
}

fun Vec3d.hasLOS(): Boolean {
    val client = MinecraftClient.getInstance()
    val playerPos = client?.player?.eyePos ?: return false
    val blockHitResult = client.world?.raycast(
        RaycastContext(
            playerPos,
            this,
            RaycastContext.ShapeType.VISUAL,
            RaycastContext.FluidHandling.NONE,
            ShapeContext.absent()
        )
    )
    return blockHitResult?.blockPos == BlockPos.ofFloored(playerPos) || blockHitResult?.type == HitResult.Type.MISS
}

fun Vec3d.inCameraFrustum(): Boolean {
    val frustum = CameraModel.lastFrustum ?: return false
    return frustum.intersectPoint(this.x, this.y, this.z)
}

fun Vec3d.canSeePlayer(): Boolean {
    return this.inCameraFrustum() && this.hasLOS()
}

fun Box.hasLOS(): Boolean {
    val minCorner = Vec3d(this.minX, this.minY, this.minZ)
    val maxCorner = Vec3d(this.maxX, this.maxY, this.maxZ)
    if (minCorner.hasLOS()) return true
    else if (maxCorner.hasLOS()) return true
    else return false
}

fun Box.getCorners(): Array<Vec3i> {
    return arrayOf(
        Vec3i(this.minX.toInt(), this.maxY.toInt(), this.minZ.toInt()),
        Vec3i(this.maxX.toInt(), this.maxY.toInt(), this.minZ.toInt()),
        Vec3i(this.minX.toInt(), this.maxY.toInt(), this.maxZ.toInt()),
        Vec3i(this.maxX.toInt(), this.maxY.toInt(), this.maxZ.toInt()),
        Vec3i(this.minX.toInt(), this.minY.toInt(), this.minZ.toInt()),
        Vec3i(this.maxX.toInt(), this.minY.toInt(), this.minZ.toInt()),
        Vec3i(this.minX.toInt(), this.minY.toInt(), this.maxZ.toInt()),
        Vec3i(this.maxX.toInt(), this.minY.toInt(), this.maxZ.toInt()),
    )
}

fun Box.getTopCorners(): Array<Vec3i> {
    return arrayOf(
        Vec3i(this.minX.toInt(), this.maxY.toInt(), this.minZ.toInt()),
        Vec3i(this.maxX.toInt(), this.maxY.toInt(), this.minZ.toInt()),
        Vec3i(this.minX.toInt(), this.maxY.toInt(), this.maxZ.toInt()),
        Vec3i(this.maxX.toInt(), this.maxY.toInt(), this.maxZ.toInt()),
    )
}

fun Box.edges(): List<Line> {
    val bottomLeftFront = Vec3d(minX, minY, minZ)
    val bottomLeftBack = Vec3d(minX, minY, maxZ)
    val topLeftFront = Vec3d(minX, maxY, minZ)
    val topLeftBack = Vec3d(minX, maxY, maxZ)
    val bottomRightFront = Vec3d(maxX, minY, minZ)
    val bottomRightBack = Vec3d(maxX, minY, maxZ)
    val topRightFront = Vec3d(maxX, maxY, minZ)
    val topRightBack = Vec3d(maxX, maxY, maxZ)

    return listOf(
        // bottom face
        Line(bottomLeftFront, bottomLeftBack),
        Line(bottomLeftBack, bottomRightBack),
        Line(bottomRightBack, bottomRightFront),
        Line(bottomRightFront, bottomLeftFront),

        // top face
        Line(topLeftFront, topLeftBack),
        Line(topLeftBack, topRightBack),
        Line(topRightBack, topRightFront),
        Line(topRightFront, topLeftFront),

        // verticals
        Line(topLeftFront, bottomLeftFront),
        Line(bottomLeftBack, topLeftBack),
        Line(topRightBack, bottomRightBack),
        Line(bottomRightFront, topRightFront),
    )
}

fun Double.formatOneDecimal(): String = "%.1f".format(this)

// this guy shouldnt be here
fun <T> MutableList<T>.getLast(): T? {
    if (this.isEmpty()) return null
    return this[this.size - 1]
}

fun Color.withAlpha(alpha: Float): Color {
    return Color(this.red / 255f, this.green / 255f, this.blue / 255f, alpha)
}