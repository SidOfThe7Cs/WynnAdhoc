package sidly.wynnadhoc.features

import com.wynntils.core.components.Services
import com.wynntils.utils.mc.McUtils
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.ItemEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.sound.SoundEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.event.entity.ForEachEntityEvent
import sidly.wynnadhoc.event.entity.MobRenderData
import sidly.wynnadhoc.utils.ChatMessageUtils
import sidly.wynnadhoc.utils.FormatUtils
import sidly.wynnadhoc.utils.datatypes.playerCanSee
import sidly.wynnadhoc.utils.getVehicleHitboxFallback

object ItemBoxes {
    private val config get() = ConfigManager.INSTANCE.config.itemBox
    private var lastSoundPlayedAt = -1L

    fun onEntity(event: ForEachEntityEvent) {
        (event.entity as? ItemEntity)?.let { itemEntity ->
            if (!isMythicBox(itemEntity.stack)) return
            if (!event.entity.entityPos.playerCanSee()) return

            event.highlight(
                MobRenderData(
                    event.entity.getVehicleHitboxFallback(),
                    FormatUtils.getMythicColor(),
                    config.boxMythics,
                    config.renderArrowPointerToMythics,
                )
            )

            if (!event.isDetected) {
                event.markDetected()
                val msg = Text.literal("Mythic box").setStyle(Style.EMPTY.withColor(FormatUtils.getMythicColor().rgb))
                msg.append(Text.literal(" dropped at ").setStyle(Style.EMPTY.withColor(Formatting.LIGHT_PURPLE)))
                if (config.chatMsgMythic) ChatMessageUtils.sendChatCoords(event.entity, msg)
                try {
                    if (config.playSoundMythic) {
                        if (lastSoundPlayedAt + 600 < System.currentTimeMillis()) {
                            McUtils.playSoundMaster(
                                SoundEvent.of(
                                    Identifier.of(
                                        "wynntils",
                                        "misc.mythic-found-classic"
                                    )
                                )
                            )
                        }
                        lastSoundPlayedAt = System.currentTimeMillis()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun isMythicBox(itemStack: ItemStack): Boolean {
        return itemStack.item == Items.POTION && itemStack
            .get(DataComponentTypes.CUSTOM_MODEL_DATA)
            ?.floats()
            ?.contains(
                Services.CustomModel.getFloat("mythic_box")
                    .orElse(-1f)
            ) ?: false
    }
}