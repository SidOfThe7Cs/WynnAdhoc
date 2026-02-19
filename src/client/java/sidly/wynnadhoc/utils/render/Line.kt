package sidly.wynnadhoc.utils.render

import net.minecraft.util.math.Vec3d

data class Line(val p1: Vec3d, val p2: Vec3d) {
    val direction: Vec3d = p2.subtract(p1).normalize()
}