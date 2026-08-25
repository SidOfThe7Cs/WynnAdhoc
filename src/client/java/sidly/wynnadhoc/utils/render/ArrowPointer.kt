package sidly.wynnadhoc.utils.render

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.math.Vec3d
import org.joml.Vector2d
import sidly.wynnadhoc.event.ClientTickEvent
import sidly.wynnadhoc.event.HudRenderEvent
import java.awt.Color
import kotlin.math.*

object ArrowPointer {
    private const val RADIUS = 0.12

    // TODO rework all of this maybe when vulcan exists?
    class Pointer(private val destination: Vec3d, private val color: Color) {
        fun draw(event: HudRenderEvent) {
            val camera = MinecraftClient.getInstance().cameraEntity ?: return
            val cameraPos = camera.eyePos
            val dx = destination.x - cameraPos.x
            val dz = destination.z - cameraPos.z
            val dirAngle = atan2(-dx, dz) % (PI * 2)
            val camAngle = Math.toRadians((camera.yaw % 360).toDouble())
            val angle = dirAngle - camAngle - PI / 2
            val radius = event.context.scaledWindowHeight * RADIUS
            val center = Vector2d(event.context.scaledWindowWidth / 2.0, event.context.scaledWindowHeight / 2.0)
            //val pointOnCircle = getClosestPointOnCircle(screenCoords, center, radius)
            /*
            val screenCoords = RenderUtils.worldToScreenCoords(destination) ?: return
            val dx = screenCoords.x - center.x
            val dy = screenCoords.y - center.y

            val angle = atan2(dy, dx) // radians

             */

            drawRingArc(event.context, center, radius, radius - 1.5, angle, color)

            val size = event.context.scaledWindowHeight * 0.009
            val basePoints = listOf(
                Vector2d(-size, -size), // top-left
                Vector2d(size, -size),  // top-right
                Vector2d(size, size),   // bottom-right
                Vector2d(-size, size)   // bottom-left
            )

            val cos = cos(angle - Math.toRadians(45.0))
            val sin = sin(angle - Math.toRadians(45.0))

            /*
            val rotatedPoints = basePoints.map { p ->
                rotatePoint(Vector2d(pointOnCircle.x + p.x, pointOnCircle.y + p.y), pointOnCircle, cos, sin)
            }

            event.context.drawFilledQuad(
                rotatedPoints[1],
                rotatedPoints[1],
                rotatedPoints[2],
                rotatedPoints[3],
                color.rgb
            )

             */
        }
    }

    private fun rotatePoint(point: Vector2d, center: Vector2d, cos: Double, sin: Double): Vector2d {
        val dx = point.x - center.x
        val dy = point.y - center.y
        return Vector2d(
            center.x + dx * cos - dy * sin,
            center.y + dx * sin + dy * cos
        )
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