package sidly.wynnadhoc.features

import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.math.random.Random
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.event.entity.ForEachEntityEvent
import sidly.wynnadhoc.utils.FormatUtils
import sidly.wynnadhoc.utils.datatypes.TimeLimitedMap
import sidly.wynnadhoc.utils.datatypes.toBlockPos
import java.util.concurrent.TimeUnit

object ShamanTotem {
    private val timeRemainingRegex = Regex("(?<timeLeft>\\d+)s")
    private val totemDurationMap = TimeLimitedMap<Int, Int>(2, TimeUnit.SECONDS)
    private val config get() = ConfigManager.INSTANCE.config.shaman

    fun onEntity(event: ForEachEntityEvent) {
        if (!config.totemWarningToggle) return
        val playerName = MinecraftClient.getInstance().player?.name?.string ?: return
        val textParser = event.textParser ?: return
        val index = textParser.findIndex({ part ->
            part.string.startsWith(playerName) && part.getColorRgb() == 0x55FFFF
        })
        if (index == -1) return
        textParser.get(index + 1)?.let { part ->
            if (part.string == "Totem" && part.getColorRgb() == 0xAAAAAA) {
                val durationSymbol =
                    textParser.findIndex({ part -> part.string == "\uE01F" && part.getColorRgb() == 0xFF55FF })
                var remaining = -1
                textParser.find({ part ->
                    val matches = timeRemainingRegex.find(part.string)
                    remaining = matches?.groups?.get("timeLeft")?.value?.toInt() ?: -1
                    val prev = totemDurationMap[event.id] ?: -1
                    if (prev != -1 && remaining != -1 && prev != remaining) {
                        onTotemDuration(remaining, prev)
                    }
                    matches != null
                }, durationSymbol)
                totemDurationMap.put(event.id, remaining)

                /*
                val box = Box(pos.x - 0.1, pos.y - 1.6, pos.z - 0.1, pos.x + 0.1, pos.y - 1.4, pos.z + 0.1)
                MinecraftClient.getInstance().world?.getEntitiesByClass(
                    DisplayEntity.ItemDisplayEntity::class.java,
                    box,
                    { true })?.forEach { totemEntity ->
                    // this also includes the totem ring
                }

                 */
            }
        }
    }

    fun onTotemDuration(current: Int, previous: Int) {
        if (!config.totemWarningToggle) return
        if (current > previous) return
        if (current == config.durationToWarn) {
            totemWarn(current)
        } else if (config.repeatWarn && current < config.durationToWarn) {
            totemWarn(current)
        }
    }

    fun totemWarn(remaining: Int) {
        if (config.playSound) {
            val soundInstance = PositionedSoundInstance(
                SoundEvents.BLOCK_BELL_USE,
                SoundCategory.MASTER,
                config.volume,
                1.0f,
                Random.create(0),
                MinecraftClient.getInstance().player?.entityPos?.toBlockPos(),
            )
            MinecraftClient.getInstance().soundManager.play(soundInstance)
        }
        if (config.showTitle) {
            val text = Text.literal("Totem: $remaining").setStyle(Style.EMPTY.withColor(FormatUtils.randomColor()))
            MinecraftClient.getInstance().inGameHud.setTitle(text)
        }
    }
}
