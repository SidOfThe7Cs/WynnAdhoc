package sidly.wynnadhoc.models

import net.minecraft.client.MinecraftClient
import sidly.wynnadhoc.utils.ItemUtils

object Character {
    var uuid = ""

    fun onTick() {
        if (uuid.isNotEmpty()) return
        val compass = MinecraftClient.getInstance().player?.inventory?.mainStacks?.get(7) ?: return
        val lore = ItemUtils.getLore(compass)
        uuid = lore.getOrNull(0)?.string ?: return
    }

    fun onWorldChange() {
        uuid = ""
    }
}