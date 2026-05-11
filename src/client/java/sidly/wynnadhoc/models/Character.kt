package sidly.wynnadhoc.models

import com.wynntils.core.components.Models

object Character {
    fun uuid(): String {
        return try {
            Models.Character.id
        } catch (_: Exception) {
            "-"
        }
    }
}