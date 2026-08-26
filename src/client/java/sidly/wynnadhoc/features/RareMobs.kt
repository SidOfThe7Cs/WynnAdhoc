package sidly.wynnadhoc.features

import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.entity.decoration.DisplayEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.random.Random
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.event.entity.ForEachEntityRenderEvent
import sidly.wynnadhoc.utils.ChatMessageUtils
import sidly.wynnadhoc.utils.FormatUtils
import sidly.wynnadhoc.utils.datatypes.TimeLimitedSet
import sidly.wynnadhoc.utils.datatypes.formatOneDecimal
import sidly.wynnadhoc.utils.getVehicleHitbox
import sidly.wynnadhoc.utils.render.ArrowPointer
import sidly.wynnadhoc.utils.render.drawBox
import sidly.wynnadhoc.utils.text.TextDisplayParser
import java.util.concurrent.TimeUnit

object RareMobs {
    private val rareMobs = TimeLimitedSet<Int>(120, TimeUnit.SECONDS)
    private val config get() = ConfigManager.INSTANCE.config.rareMob

    fun onEachEntity(event: ForEachEntityRenderEvent) {
        if (!config.mainToggle) return
        if (event.entity is DisplayEntity.TextDisplayEntity) {
            val textDisplayParser = TextDisplayParser(event.entity)
            if (textDisplayParser.isRareMob()) {
                // new spawn (cant use .isnew as the textdisplay is sent to the client and then updated at a later point) i think
                if (!rareMobs.contains(event.entity.id)) {
                    rareMobs.put(event.entity.id)

                    if (config.chatMsg) {
                        val name = event.entity.text.siblings.getOrNull(2)?.string ?: "Unknown"
                        val formattedX = event.entity.x.formatOneDecimal()
                        val formattedY = event.entity.y.formatOneDecimal()
                        val formattedZ = event.entity.z.formatOneDecimal()
                        ChatMessageUtils.sendChatMessage("rare mob \"$name\" spawned at $formattedX $formattedY $formattedZ")
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

                if (config.boxRareMobs) {
                    event.renderEvent.drawBox(event.entity.getVehicleHitbox(), FormatUtils.getMythicColor())
                }

                if (config.renderArrowPointer) {
                    ArrowPointer.addPointer(ArrowPointer.Pointer(event.entity.entityPos, FormatUtils.getMythicColor()))
                }
            }
        }
    }
}
