package sidly.wynnadhoc.event.entity

import net.minecraft.util.math.Box
import sidly.wynnadhoc.event.WorldRenderEvent
import sidly.wynnadhoc.utils.render.ArrowPointer
import sidly.wynnadhoc.utils.render.drawBox
import java.awt.Color

class MobRenderData(val box: Box, val color: Color, val shouldBox: Boolean = true, val shouldArrow: Boolean = true) {
    companion object {
        fun onWorldRender(event: WorldRenderEvent) {
            BaseForEachEntityEvent.toRender().forEach { data ->
                if (data.shouldBox) {
                    event.drawBox(data.box, data.color)
                }

                if (data.shouldArrow) {
                    ArrowPointer.addPointer(ArrowPointer.Pointer(data.box.center, data.color))
                }
            }
        }
    }
}