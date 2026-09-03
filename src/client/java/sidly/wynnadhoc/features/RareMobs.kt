package sidly.wynnadhoc.features

import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.math.random.Random
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.event.entity.ForEachEntityEvent
import sidly.wynnadhoc.event.entity.MobRenderData
import sidly.wynnadhoc.utils.ChatMessageUtils
import sidly.wynnadhoc.utils.FormatUtils
import sidly.wynnadhoc.utils.getVehicleHitboxFallback

object RareMobs {
    private val config get() = ConfigManager.INSTANCE.config.rareMob

    fun onEachEntity(event: ForEachEntityEvent) {
        if (!config.mainToggle) return
        if (event.textParser?.isRareMob() != true) return

        event.highlight(
            MobRenderData(
                event.entity.getVehicleHitboxFallback(),
                FormatUtils.getMythicColor(),
                config.boxRareMobs,
                config.renderArrowPointer,
            )
        )

        // new spawn
        if (!event.isDetected) {
            event.markDetected()

            val name = event.textParser.find({ part -> part.isNotEmpty() }, 1)?.string ?: "Unknown"
            val msg = Text.literal("rare mob \"$name\" found at ")

            if (config.usePartyChat) {
                val partyMsg =
                    "${event.entity.x.toInt()} ${event.entity.y.toInt()} ${event.entity.z.toInt()} found a $name"
                ChatMessageUtils.sendChatCommand("p $partyMsg")
            }

            if (config.chatMsg) {
                ChatMessageUtils.sendChatCoords(event.entity, msg)
            }

            if (config.playSound) {
                val soundInstance = PositionedSoundInstance(
                    SoundEvents.ENTITY_WITHER_SPAWN,
                    SoundCategory.BLOCKS,
                    config.volume,
                    1.0f,
                    Random.create(0),
                    event.entity.blockPos,
                )
                MinecraftClient.getInstance().soundManager.play(soundInstance)
            }
        }
    }
}
