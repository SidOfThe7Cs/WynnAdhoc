package sidly.wynnadhoc.features.raid

import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.event.entity.ForEachEntityEvent
import sidly.wynnadhoc.event.entity.MobRenderData
import sidly.wynnadhoc.utils.getVehicleHitboxFallback
import sidly.wynnadhoc.utils.playerCanSee
import java.awt.Color

object TNA {
    private val config get() = ConfigManager.INSTANCE.config.tna

    fun onEachEntity(event: ForEachEntityEvent) {
        val textParser = event.textParser ?: return
        if (config.boxShadowlings && textParser.isShadowling() || config.boxBulbCatchers && textParser.isBulbCatcher()) {
            if (!event.entity.playerCanSee()) return
            event.highlight(
                MobRenderData(
                    event.entity.getVehicleHitboxFallback(),
                    Color.GREEN,
                    config.boxShadowlings,
                    config.renderArrowPointerShadow,
                )
            )
        }
    }
}