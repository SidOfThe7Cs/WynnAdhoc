package sidly.wynnadhoc.utils.render

import com.mojang.blaze3d.pipeline.BlendFunction
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gl.UniformType
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.Identifier
import sidly.wynnadhoc.WynnAdhocClient

enum class SkyHanniRenderPipeline(
    snippet: RenderPipeline.Snippet,
    vFormat: VertexFormat = VertexFormats.POSITION_COLOR,
    vDrawMode: VertexFormat.DrawMode = VertexFormat.DrawMode.QUADS,
    blend: BlendFunction? = null,
    withCull: Boolean? = false,
    vertexShaderPath: String? = null,
    fragmentShaderPath: String? = vertexShaderPath,
    sampler: String? = null,
    uniforms: Map<String, UniformType> = emptyMap(),
    depthWrite: Boolean = true,
    depthTestFunction: DepthTestFunction = DepthTestFunction.LEQUAL_DEPTH_TEST,
) {
    LINES(
        snippet = RenderPipelines.RENDERTYPE_LINES_SNIPPET,
        vFormat = VertexFormats.POSITION_COLOR_NORMAL,
        vDrawMode = VertexFormat.DrawMode.LINES,
    ),
    LINES_XRAY(
        snippet = RenderPipelines.RENDERTYPE_LINES_SNIPPET,
        vFormat = VertexFormats.POSITION_COLOR_NORMAL,
        vDrawMode = VertexFormat.DrawMode.LINES,
        depthWrite = false,
        depthTestFunction = DepthTestFunction.NO_DEPTH_TEST,
    ),
    FILLED(
        snippet = RenderPipelines.POSITION_COLOR_SNIPPET,
        vDrawMode = VertexFormat.DrawMode.TRIANGLE_STRIP,
    ),
    FILLED_XRAY(
        snippet = RenderPipelines.POSITION_COLOR_SNIPPET,
        vDrawMode = VertexFormat.DrawMode.TRIANGLE_STRIP,
        depthWrite = false,
        depthTestFunction = DepthTestFunction.NO_DEPTH_TEST,
    ),
    TRIANGLES(
        snippet = RenderPipelines.POSITION_COLOR_SNIPPET,
        vDrawMode = VertexFormat.DrawMode.TRIANGLES,
    ),
    TRIANGLES_XRAY(
        snippet = RenderPipelines.POSITION_COLOR_SNIPPET,
        vDrawMode = VertexFormat.DrawMode.TRIANGLES,
        depthWrite = false,
        depthTestFunction = DepthTestFunction.NO_DEPTH_TEST,
    ),
    TRIANGLE_FAN(
        snippet = RenderPipelines.POSITION_COLOR_SNIPPET,
        vDrawMode = VertexFormat.DrawMode.TRIANGLE_FAN,
    ),
    TRIANGLE_FAN_XRAY(
        snippet = RenderPipelines.POSITION_COLOR_SNIPPET,
        vDrawMode = VertexFormat.DrawMode.TRIANGLE_FAN,
        depthWrite = false,
        depthTestFunction = DepthTestFunction.NO_DEPTH_TEST,
    ),
    QUADS(
        snippet = RenderPipelines.POSITION_COLOR_SNIPPET,
    ),
    QUADS_XRAY(
        snippet = RenderPipelines.POSITION_COLOR_SNIPPET,
        depthWrite = false,
        depthTestFunction = DepthTestFunction.NO_DEPTH_TEST,
    ), ;

    private val _pipe: RenderPipeline = RenderPipelines.register(
        RenderPipeline.builder(snippet)
            .withLocation(Identifier.of(WynnAdhocClient.MOD_ID, this.name.lowercase()))
            .withVertexFormat(vFormat, vDrawMode).apply {
                // One or the other, never both
                blend?.let(this::withBlend) ?: withCull?.let(this::withCull)
                vertexShaderPath?.let { withVertexShader(Identifier.of(WynnAdhocClient.MOD_ID, it)) }
                fragmentShaderPath?.let {
                    withFragmentShader(
                        Identifier.of(
                            WynnAdhocClient.MOD_ID, it
                        )
                    )
                }
                sampler?.let(this::withSampler)
                uniforms.forEach(this::withUniform)
                withDepthWrite(depthWrite)
                withDepthTestFunction(depthTestFunction)
            }.build(),
    )

    operator fun invoke(): RenderPipeline = _pipe
}
