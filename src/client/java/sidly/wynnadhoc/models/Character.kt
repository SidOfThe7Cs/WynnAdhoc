package sidly.wynnadhoc.models

import com.wynntils.core.components.Models
import net.minecraft.client.MinecraftClient
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import sidly.wynnadhoc.mixin.client.accessors.WynntillsEventBusAccessor
import java.util.regex.Pattern

object Character {
    private val CHARACTER_ID_PATTERN: Pattern = Pattern.compile("^[a-z0-9]{8}$")
    private val MINECRAFT_FORMATTING_CODE_PATTERN: Pattern = Pattern.compile("§[0-9a-fk-or]")

    fun uuid(): String {
        if (WynntillsEventBusAccessor.getEventBus() == null) {
            return "-"
        }
        return try {
            Models.Character.id
        } catch (_: Exception) {
            return try {
                getCurrentCharacterIdFromCompass()
            } catch (_: Exception) {
                "-"
            }
        }
    }

    private fun getCurrentCharacterIdFromCompass(): String {
        val client = MinecraftClient.getInstance()
        if (client == null || client.player == null) return "-"
        val compass = client.player!!.getInventory().getStack(7)
        if (compass == null || compass.isEmpty || compass.getComponents() == null) return "-"

        val lore = compass.getComponents().get<LoreComponent?>(DataComponentTypes.LORE)
        if (lore == null || lore.lines().isEmpty()) return "-"

        for (line in lore.lines()) {
            val text =
                MINECRAFT_FORMATTING_CODE_PATTERN.matcher(line.string)
                    .replaceAll("").trim { it <= ' ' }
            if (CHARACTER_ID_PATTERN.matcher(text).matches()) {
                return text
            }
        }
        return "-"
    }
}