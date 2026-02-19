package sidly.wynnadhoc.utils.render

import net.minecraft.client.render.LayeringTransform
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderSetup

object RenderLayers {

    fun getFilled(xray: Boolean): RenderLayer {
        return if (!xray) {
            RenderLayer.of(
                "wynnadhoc_" + SkyHanniRenderPipeline.FILLED.name,
                RenderSetup.builder(SkyHanniRenderPipeline.FILLED()).build()
            )
        } else {
            RenderLayer.of(
                "wynnadhoc_" + SkyHanniRenderPipeline.FILLED_XRAY.name,
                RenderSetup.builder(SkyHanniRenderPipeline.FILLED_XRAY()).build()
            )
        }
    }

    // why cant i get this to work
    fun getLines(throughWalls: Boolean): RenderLayer {
        return if (throughWalls) RenderLayer.of(
            "skyhanni_lines_xray",
            RenderSetup.builder(SkyHanniRenderPipeline.LINES_XRAY())
                .layeringTransform(LayeringTransform.NO_LAYERING)
                .build(),
        ) else RenderLayer.of(
            "skyhanni_lines",
            RenderSetup.builder(SkyHanniRenderPipeline.LINES())
                .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                .build(),
        )
    }

}