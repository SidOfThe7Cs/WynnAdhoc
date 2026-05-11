package sidly.wynnadhoc.models

import com.wynntils.core.components.Models
import sidly.wynnadhoc.mixin.client.accessors.WynntillsEventBusAccessor

object Character {
    fun uuid(): String {
        if (WynntillsEventBusAccessor.getEventBus() == null) {
            return "-"
        }
        return try {
            Models.Character.id
        } catch (_: Exception) {
            "-"
        }
    }
}