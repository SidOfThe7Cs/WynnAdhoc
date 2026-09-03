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
import sidly.wynnadhoc.utils.getVehicleHitboxFallback

object ItemBoxes {
    private val config get() = ConfigManager.INSTANCE.config.itemBox

    fun onEntity(event: ForEachEntityEvent) {
        (event.entity as? ItemEntity)?.let { itemEntity ->
            if (!isMythicBox(itemEntity.stack)) return

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
                msg.append(Text.literal(" dropped at ").setStyle(Style.EMPTY.withColor(Formatting.WHITE)))
                if (config.chatMsgMythic) ChatMessageUtils.sendChatCoords(event.entity, msg)
                try {
                    if (config.playSoundMythic) McUtils.playSoundMaster(
                        SoundEvent.of(
                            Identifier.of(
                                "wynntils",
                                "misc.mythic-found-classic"
                            )
                        )
                    )
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