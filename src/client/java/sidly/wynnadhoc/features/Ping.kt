package sidly.wynnadhoc.features

import net.minecraft.client.MinecraftClient
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Vec3d
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.config.catagories.PingConfig
import sidly.wynnadhoc.event.ChatMessageEvent
import sidly.wynnadhoc.event.KeyboardEvent
import sidly.wynnadhoc.event.MouseButtonEvent
import sidly.wynnadhoc.event.WorldRenderEvent
import sidly.wynnadhoc.utils.ChatMessageUtils
import sidly.wynnadhoc.utils.datatypes.TimeLimitedSet
import sidly.wynnadhoc.utils.render.ArrowPointer
import sidly.wynnadhoc.utils.render.drawPing
import java.awt.Color
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object Ping {
    private val wynnmodPingPattern: Pattern =
        Pattern.compile("ping:\\s*(?<x>-?\\d+\\.\\d+),\\s*(?<y>-?\\d+\\.\\d+),\\s*(?<z>-?\\d+\\.\\d+)$")

    private fun config(): PingConfig {
        return ConfigManager.INSTANCE.config.ping
    }

    private var waypoints = TimeLimitedSet<Vec3d>(60, TimeUnit.SECONDS)
    private var lastDuration = 60.0
    private var lastPing: Vec3d? = null

    fun onChat(event: ChatMessageEvent) {
        if (!config().renderWynnmodPings) return

        if (config().pingDuration != lastDuration) {
            lastDuration = config().pingDuration
            waypoints = TimeLimitedSet<Vec3d>(lastDuration.toLong(), TimeUnit.SECONDS)
        }

        val msg = event.strippedByWynntills.lowercase(Locale.getDefault())
        val pingMatcher = wynnmodPingPattern.matcher(msg)
        if (pingMatcher.find()) {
            val x = pingMatcher.group("x").toDouble()
            val y = pingMatcher.group("y").toDouble()
            val z = pingMatcher.group("z").toDouble()

            val loc = Vec3d(x, y, z)
            waypoints.put(loc)
            lastPing = loc
        }
    }

    fun onWorldRender(event: WorldRenderEvent) {
        if (waypoints.isEmpty) lastPing = null
        if (config().onlyMostRecent) lastPing?.let { renderPing(event, it) }
        else waypoints.stream().forEach { w: Vec3d -> renderPing(event, w) }
    }

    private fun renderPing(event: WorldRenderEvent, loc: Vec3d) {
        if (config().renderArrowPointer) ArrowPointer.addPointer(ArrowPointer.Pointer(loc, Color.RED))
        event.drawPing(loc)
    }

    fun onMouseButton(event: MouseButtonEvent) {
        if (event.isPress && event.input.button == config().pingKeybind) {
            sendPing()
        }
    }

    fun onKeyPressed(event: KeyboardEvent) {
        if (event.action == 1 && event.key == config().pingKeybind) {
            sendPing()
        }
    }

    private fun sendPing() {
        val client = MinecraftClient.getInstance()
        val entity = client.cameraEntity
        val world = client.world

        if (entity != null && world != null) {
            // Get the block the player is looking at
            val hitResult = entity.raycast(120.0, 0.0f, false) // 100 blocks range

            if (hitResult != null && hitResult.type != HitResult.Type.MISS) {
                // Get the position where the ray hits
                val pos = hitResult.pos

                // Format coordinates with one decimal place
                val formattedCoords = String.format("%.1f, %.1f, %.1f", pos.x, pos.y, pos.z)
                ChatMessageUtils.sendChatCommand("p wynnmod ping: $formattedCoords")
            }
        }
    }
}
