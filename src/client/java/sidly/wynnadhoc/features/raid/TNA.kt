package sidly.wynnadhoc.features.raid

import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.event.entity.ForEachEntityEvent
import sidly.wynnadhoc.event.entity.MobRenderData
import sidly.wynnadhoc.utils.getVehicleHitboxFallback
import java.awt.Color

object TNA {
    private val config get() = ConfigManager.INSTANCE.config.tna

    fun onEachEntity(event: ForEachEntityEvent) {
        if (!config.boxShadowlings) return
        if (event.textParser?.isShadowling() != true) return

        event.highlight(
            MobRenderData(
                event.entity.getVehicleHitboxFallback(),
                Color.GREEN,
                config.boxShadowlings,
                config.renderArrowPointer,
            )
        )
    }
}