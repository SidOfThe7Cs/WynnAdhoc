package sidly.wynnadhoc.utils.render

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.math.Vec3d
import org.joml.Vector2d
import sidly.wynnadhoc.event.ClientTickEvent
import sidly.wynnadhoc.event.HudRenderEvent
import sidly.wynnadhoc.utils.datatypes.withAlpha
import java.awt.Color
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object ArrowPointer {
    private const val RADIUS = 0.12
    private const val MAX_DISTANCE = 200

    class Pointer(private val destination: Vec3d, private val color: Color) {
        fun draw(event: HudRenderEvent) {
            val screenCoords = RenderUtils.worldToScreenCoords(destination) ?: return
            val radius = event.context.scaledWindowHeight * RADIUS
            val center = Vector2d(event.context.scaledWindowWidth / 2.0, event.context.scaledWindowHeight / 2.0)
            val dx = screenCoords.x - center.x
            val dy = screenCoords.y - center.y
            val angle = atan2(dy, dx)
            val pointOnCircle = getClosestPointOnCircle(screenCoords, center, radius)
            val dist = MinecraftClient.getInstance().player?.squaredDistanceTo(destination) ?: 0.0
            val alphaColor = color.withAlpha(calculateAlpha(dist))

            drawRingArc(event.context, center, radius, radius - 1.5, angle, alphaColor)

            val p1 = Vector2d(pointOnCircle.x - 3.0, pointOnCircle.y - 3.0)
            val p2 = Vector2d(pointOnCircle.x + 3.0, pointOnCircle.y - 3.0)
            val p3 = Vector2d(pointOnCircle.x + 3.0, pointOnCircle.y + 3.0)
            val p4 = Vector2d(pointOnCircle.x - 3.0, pointOnCircle.y + 3.0)

            event.context.drawFilledQuad(p1, p2, p3, p4, Color.GREEN.rgb)
        }
    }

    private fun drawRingArc(
        context: DrawContext,
        center: Vector2d,
        outerRadius: Double,
        innerRadius: Double,
        angle: Double,
        color: Color
    ) {
        val segments = 20
        val arcAngle = Math.PI * 0.2
        val startAngle = angle - arcAngle / 2
        val endAngle = angle + arcAngle / 2

        // Draw as a strip of quads
        for (i in 0 until segments) {
            val t1 = i.toDouble() / segments
            val t2 = (i + 1).toDouble() / segments
            val a1 = startAngle + (endAngle - startAngle) * t1
            val a2 = startAngle + (endAngle - startAngle) * t2

            val x1 = center.x + outerRadius * cos(a1)
            val y1 = center.y + outerRadius * sin(a1)
            val x2 = center.x + outerRadius * cos(a2)
            val y2 = center.y + outerRadius * sin(a2)
            val x3 = center.x + innerRadius * cos(a2)
            val y3 = center.y + innerRadius * sin(a2)
            val x4 = center.x + innerRadius * cos(a1)
            val y4 = center.y + innerRadius * sin(a1)

            val minX = minOf(x1, x2, x3, x4).toInt()
            val maxX = maxOf(x1, x2, x3, x4).toInt()
            val minY = minOf(y1, y2, y3, y4).toInt()
            val maxY = maxOf(y1, y2, y3, y4).toInt()

            context.fill(minX, minY, maxX + 1, maxY + 1, color.rgb)
        }
    }

    private fun calculateAlpha(distanceSQ: Double): Float {
        return if (distanceSQ < MAX_DISTANCE * MAX_DISTANCE) (0.3 + 0.7 * (1.0 - distanceSQ / (MAX_DISTANCE * MAX_DISTANCE))).toFloat() else 0.0f
    }

    fun getClosestPointOnCircle(screenCoords: Vector2d, center: Vector2d, radius: Double): Vector2d {
        // Calculate vector from center to screenCoords
        val dx = screenCoords.x - center.x
        val dy = screenCoords.y - center.y

        // Calculate distance from center to screenCoords
        val distance = sqrt(dx * dx + dy * dy)

        // If the point is exactly at the center, return any point on the circle
        if (distance == 0.0) {
            return Vector2d(center.x, center.y - radius)
        }

        // Normalize the direction vector and scale by radius
        val normalizedX = dx / distance
        val normalizedY = dy / distance

        // Return the point on the circle in that direction
        return Vector2d(
            center.x + normalizedX * radius,
            center.y + normalizedY * radius
        )
    }

    private val pointers = mutableListOf<Pointer>()

    fun addPointer(pointer: Pointer) {
        pointers.add(pointer)
    }

    fun onScreenRender(event: HudRenderEvent) {
        pointers.forEach { p -> p.draw(event) }
    }

    fun onTick(event: ClientTickEvent) {
        pointers.clear()
    }
}