package sidly.wynnadhoc.features

import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.entity.decoration.DisplayEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.math.random.Random
import sidly.wynnadhoc.WynnAdhocClient
import sidly.wynnadhoc.event.ForEachEntityRenderEvent
import sidly.wynnadhoc.utils.ChatMessageUtils
import sidly.wynnadhoc.utils.datatypes.TimeLimitedSet
import sidly.wynnadhoc.utils.getVehicleHitbox
import sidly.wynnadhoc.utils.isRareMob
import sidly.wynnadhoc.utils.render.drawBox
import java.awt.Color
import java.util.concurrent.TimeUnit

object RareMobs {
    private val rareMobs = TimeLimitedSet<Int>(120, TimeUnit.SECONDS)

    fun onEachEntity(event: ForEachEntityRenderEvent) {
        if (event.entity is DisplayEntity.TextDisplayEntity) {
            if (event.entity.isRareMob()) {
                if (!rareMobs.contains(event.entity.id)) {
                    rareMobs.put(event.entity.id)
                    val name = event.entity.text.siblings.getOrNull(2)?.string ?: "Unknown"
                    WynnAdhocClient.LOGGER.temp("rare mob \"$name\" spawned at " + event.entity.x + " " + event.entity.y + " " + event.entity.z)
                    ChatMessageUtils.sendChatMessage("rare mob \"$name\" spawned at " + event.entity.x + " " + event.entity.y + " " + event.entity.z)

                    val soundInstance = PositionedSoundInstance(
                        SoundEvents.ENTITY_WITHER_SPAWN,
                        SoundCategory.BLOCKS,
                        1.5f,
                        1.0f,
                        Random.create(0),
                        event.entity.blockPos,
                    )
                    MinecraftClient.getInstance().soundManager.play(soundInstance)
                }
                event.renderEvent.drawBox(event.entity.getVehicleHitbox(), Color.MAGENTA)
            }
        }
    }
}
