package sidly.wynnadhoc.utils.datatypes

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import org.joml.Vector2f
import org.joml.Vector2i
import org.joml.Vector3f
import sidly.wynnadhoc.utils.render.Line

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

fun Vector2f.to2i(): Vector2i {
    return Vector2i(this.x.toInt(), this.y.toInt())
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

// this guy shouldnt be here
fun <T> MutableList<T>.getLast(): T? {
    if (this.isEmpty()) return null
    return this[this.size - 1]
}