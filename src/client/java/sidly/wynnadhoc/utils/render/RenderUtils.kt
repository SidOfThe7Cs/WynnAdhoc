package sidly.wynnadhoc.utils.render

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider.Immediate
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.joml.Vector3f
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.event.WorldRenderEvent
import sidly.wynnadhoc.utils.datatypes.edges
import sidly.wynnadhoc.utils.datatypes.toBlockPos
import sidly.wynnadhoc.utils.datatypes.toBox
import sidly.wynnadhoc.utils.datatypes.toVec3d
import java.awt.Color
import kotlin.math.ln
import kotlin.math.sqrt

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