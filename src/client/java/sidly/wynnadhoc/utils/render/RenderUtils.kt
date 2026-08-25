package sidly.wynnadhoc.utils.render

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider.Immediate
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Matrix3x2f
import org.joml.Matrix4f
import org.joml.Vector2d
import org.joml.Vector3f
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.event.WorldRenderEvent
import sidly.wynnadhoc.mixin.client.Invoker.GameRendererInvoker
import sidly.wynnadhoc.utils.datatypes.edges
import sidly.wynnadhoc.utils.datatypes.toBlockPos
import sidly.wynnadhoc.utils.datatypes.toBox
import sidly.wynnadhoc.utils.datatypes.toVec3d
import java.awt.Color
import kotlin.math.*

object RenderUtils {
    private val config get() = ConfigManager.INSTANCE.config.gui

    fun onFabricWorldRender(event: WorldRenderContext) {
        val vertexConsumers = event.consumers()
        if (vertexConsumers !is Immediate) return

        val stack = event.matrices()

        WorldRenderEvent(stack, event.gameRenderer().camera, vertexConsumers, event.worldState().time.toFloat())
    }

    fun testDraw(event: WorldRenderEvent) {
        event.drawBox(
            Box.of(Vec3d(0.5, 80.5, 2.5), 1.0, 1.0, 1.0),
            Color.RED,
            solid = true,
        )

        val testEdges = Box.of(Vec3d(0.5, 82.5, 0.5), 1.0, 1.0, 1.0)
        event.drawBox(testEdges, Color.GREEN, xray = true)

        MinecraftClient.getInstance().player?.entityPos?.let {
            event.drawBox(
                it.add(0.0, 0.0, 5.0).toBlockPos().toBox(),
                Color.BLUE,
                xray = true,
                solid = true,
            )
        }

        event.drawLineToEye(Vec3d(0.5, 82.5, 0.5), Color.ORANGE)
    }

    // where line strip
    fun drawLines(
        event: WorldRenderEvent,
        inputLines: List<Line>,
        color: Color,
        xray: Boolean,
        thickness: Double = 1.0
    ) {
        val layer = RenderLayers.getFilled(xray)
        val buf = event.vertexConsumerProvider.getBuffer(layer)
        val matrix = event.matrices.peek().positionMatrix
        val cameraPos = event.camera.cameraPos

        for (inputLine in inputLines) {
            val line = Line(
                inputLine.p1.subtract(cameraPos),
                inputLine.p2.subtract(cameraPos),
            )

            // This gives a vector perpendicular to both (points sideways relative to view)
            val dir1 = line.p1.normalize()
            val dir2 = line.p2.normalize()

            val baseLineWidth = config.defaultLineWidth * 0.0125 * thickness
            val lineWidth1 = baseLineWidth + cameraPos.distanceTo(inputLine.p1) * 0.0005 * config.lineDistanceFactor
            val lineWidth2 = baseLineWidth + cameraPos.distanceTo(inputLine.p2) * 0.0005 * config.lineDistanceFactor

            val offset1 = line.direction.crossProduct(dir1).normalize().multiply(lineWidth1)
            val offset2 = line.direction.crossProduct(dir2).normalize().multiply(lineWidth2)

            addVertex(buf, matrix, line.p1.add(offset1), color)
            addVertex(buf, matrix, line.p1.subtract(offset1), color)
            addVertex(buf, matrix, line.p2.add(offset2), color)
            addVertex(buf, matrix, line.p2.subtract(offset2), color)
        }
    }

    fun addVertex(buf: VertexConsumer, matrix: Matrix4f, point: Vec3d, color: Color) {
        buf.vertex(matrix, point.x.toFloat(), point.y.toFloat(), point.z.toFloat())
            .color(color.red, color.green, color.blue, color.alpha)
    }

    fun getMesh(points: Set<Vec3d>): List<Line> {
        if (points.size < 2) return emptyList()

        val pointList = points.toList()
        val lines = mutableListOf<Line>()

        for (i in pointList.indices) {
            for (j in i + 1 until pointList.size) {
                lines.add(Line(pointList[i], pointList[j]))
            }
        }

        return lines
    }

    fun worldToScreenCoords(worldCoords: Vec3d): Vector2d? {
        // check nulls
        val client = MinecraftClient.getInstance() ?: return null
        if (client.player == null) return null

        // get stuff
        val renderer = client.gameRenderer
        val camera = renderer.camera

        val screenWidth = client.window.scaledWidth
        val screenHeight = client.window.scaledHeight
        val cameraPos: Vec3d = camera.cameraPos
        val yaw = Math.toRadians(camera.yaw.toDouble()).toFloat()
        val pitch = Math.toRadians(-camera.pitch.toDouble()).toFloat()
        val FOV: Double = (renderer as Any as GameRendererInvoker).invokeGetFov(
            camera,
            client.renderTickCounter.dynamicDeltaTicks,
            true
        ).toDouble()

        // Calculate relative position to camera
        var dx = worldCoords.x - cameraPos.x
        val dy = worldCoords.y - cameraPos.y
        val dz = worldCoords.z - cameraPos.z
        dx *= -1.0 // we dont ask questions we just provide solutions

        // Rotate based on yaw (horizontal rotation)
        val x = dx * cos(yaw.toDouble()) - dz * sin(yaw.toDouble())
        var z = dx * sin(yaw.toDouble()) + dz * cos(yaw.toDouble())

        // Rotate based on pitch (vertical rotation)
        val y = dy * cos(pitch.toDouble()) - z * sin(pitch.toDouble())
        z = dy * sin(pitch.toDouble()) + z * cos(pitch.toDouble())

        // if behind camera dont do weird stuff
        if (z <= 0.1) {
            z = 0.1
        }


        // Perspective projection
        val scale = min(screenWidth, screenHeight) / (2.0 * tan(Math.toRadians(FOV) / 2))
        var screenX = (x / z) * scale + screenWidth / 2
        var screenY = (-y / z) * scale + screenHeight / 2

        val minSize = 4
        val edgeSize = minSize * 3
        var size = (100 / z).toInt()
        size = max(size, minSize)

        if (screenX < 0) {
            screenX = 0.0
            size = edgeSize
        }
        if (screenY < 0) {
            screenY = 0.0
            size = edgeSize
        }
        if (screenX > screenWidth) {
            screenX = screenWidth.toDouble()
            size = edgeSize
        }
        if (screenY > screenHeight) {
            screenY = screenHeight.toDouble()
            size = edgeSize
        }
        return Vector2d(screenX, screenY)
    }

    fun worldToScreenCoords(worldCoords: Vector3f): Vector2d? {
        val coords = Vec3d(worldCoords.x.toDouble(), worldCoords.y.toDouble(), worldCoords.z.toDouble())
        return worldToScreenCoords(coords)
    }
}

fun DrawContext.drawFilledQuad(
    p1: Vector2d,
    p2: Vector2d,
    p3: Vector2d,
    p4: Vector2d,
    color: Int,
) {
    val points = listOf(p1, p2, p3, p4)

    val centerX = points.sumOf { it.x } / 4.0
    val centerY = points.sumOf { it.y } / 4.0

    val sorted = points.sortedWith { a, b ->
        val angleA = atan2(a.y - centerY, a.x - centerX)
        val angleB = atan2(b.y - centerY, b.x - centerX)
        angleA.compareTo(angleB)
    }

    this.state.addSimpleElement(
        CustomQuadRenderState(
            Matrix3x2f(this.matrices),
            sorted[3],
            sorted[2],
            sorted[1],
            sorted[0],
            color,
            this.scissorStack.peekLast(),
        )
    )
}

fun List<Vec3d>.toLines(): List<Line> {
    if (this.size < 2) return emptyList()

    val result = mutableListOf<Line>()

    var last: Vec3d? = null
    for (point in this) {
        if (last == null) {
            last = point
            continue
        }
        result.add(Line(last, point))
    }

    return result
}

fun WorldRenderEvent.drawLines(lines: List<Line>, color: Color, xray: Boolean) {
    RenderUtils.drawLines(this, lines, color, xray)
}

fun WorldRenderEvent.drawLine(line: Line, color: Color, xray: Boolean) {
    RenderUtils.drawLines(this, mutableListOf(line), color, xray)
}

fun WorldRenderEvent.drawLine(start: Vec3d?, end: Vec3d?, color: Color, xray: Boolean) {
    if (start == null || end == null) return
    RenderUtils.drawLines(this, mutableListOf(Line(start, end)), color, xray)
}

// TODO this acts weird when the target is behind you make it normal
fun WorldRenderEvent.drawLineToEye(end: Vec3d?, color: Color, xray: Boolean = true) {
    if (end == null) return
    val lookDirection = this.camera.rotation.transform(Vector3f(0f, 0f, -1f)).toVec3d()
    val line = Line(this.camera.cameraPos.add(lookDirection.multiply(2.0)), end)
    RenderUtils.drawLines(this, mutableListOf(line), color, xray)
}

fun WorldRenderEvent.drawPing(
    loc: Vec3d,
    size: Double = 0.3,
    alphaMultiplier: Float = 0.5f,
    thicknessMultiplier: Double = 1.0,
    color: Color = Color.RED,
) {
    val points: MutableSet<Vec3d> = mutableSetOf()
    points.add(loc.add(size, 0.0, 0.0))
    points.add(loc.add(-size, 0.0, 0.0))
    points.add(loc.add(0.0, size, 0.0))
    points.add(loc.add(0.0, -size, 0.0))
    points.add(loc.add(0.0, 0.0, size))
    points.add(loc.add(0.0, 0.0, -size))

    val mesh = RenderUtils.getMesh(points)
    RenderUtils.drawLines(this, mesh, color, true, thicknessMultiplier)
}

fun WorldRenderEvent.drawBox(
    box: Box?,
    color: Color,
    alphaMultiplier: Float = 1f,
    solid: Boolean = false,
    xray: Boolean = true,
    thicknessMultiplier: Double = 1.0,
) {

    /*
    if (this.isCurrentlyDeferring) {
        DeferredDrawer.deferBox(
            aabb,
            color,
            alphaMultiplier,
            depth = !seeThroughBlocks,
        )
        return
    }
     */
    if (box == null) return
    if (solid) {
        val cameraPos = camera.cameraPos
        val effectiveAABB = Box(
            box.minX - cameraPos.x, box.minY - cameraPos.y, box.minZ - cameraPos.z,
            box.maxX - cameraPos.x, box.maxY - cameraPos.y, box.maxZ - cameraPos.z,
        )

        val layer = RenderLayers.getFilled(xray)
        val buf = vertexConsumerProvider.getBuffer(layer)
        matrices.push()

        addChainedFilledBoxVertices(
            matrices,
            buf,
            effectiveAABB.minX, effectiveAABB.minY, effectiveAABB.minZ,
            effectiveAABB.maxX, effectiveAABB.maxY, effectiveAABB.maxZ,
            color.red / 255f * 0.9f,
            color.green / 255f * 0.9f,
            color.blue / 255f * 0.9f,
            color.alpha / 255f * alphaMultiplier,
        )
        matrices.pop()
    } else {
        RenderUtils.drawLines(this, box.edges(), color, xray, thicknessMultiplier)
    }
}

private fun addChainedFilledBoxVertices(
    matrices: MatrixStack,
    vertexConsumer: VertexConsumer,
    d: Double,
    e: Double,
    f: Double,
    g: Double,
    h: Double,
    i: Double,
    j: Float,
    k: Float,
    l: Float,
    m: Float
) {
    addChainedFilledBoxVertices(
        matrices,
        vertexConsumer,
        d.toFloat(),
        e.toFloat(),
        f.toFloat(),
        g.toFloat(),
        h.toFloat(),
        i.toFloat(),
        j,
        k,
        l,
        m
    )
}

private fun addChainedFilledBoxVertices(
    matrices: MatrixStack,
    vertexConsumer: VertexConsumer,
    f: Float,
    g: Float,
    h: Float,
    i: Float,
    j: Float,
    k: Float,
    l: Float,
    m: Float,
    n: Float,
    o: Float
) {
    val matrix4f = matrices.peek().positionMatrix
    vertexConsumer.vertex(matrix4f, f, g, h).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, f, g, h).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, f, g, h).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, f, g, k).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, f, j, h).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, f, j, k).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, f, j, k).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, f, g, k).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, i, j, k).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, i, g, k).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, i, g, k).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, i, g, h).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, i, j, k).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, i, j, h).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, i, j, h).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, i, g, h).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, f, j, h).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, f, g, h).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, f, g, h).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, i, g, h).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, f, g, k).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, i, g, k).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, i, g, k).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, f, j, h).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, f, j, h).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, f, j, k).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, i, j, h).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, i, j, k).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, i, j, k).color(l, m, n, o)
    vertexConsumer.vertex(matrix4f, i, j, k).color(l, m, n, o)
}