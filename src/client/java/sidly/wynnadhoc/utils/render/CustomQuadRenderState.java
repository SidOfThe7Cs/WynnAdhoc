package sidly.wynnadhoc.utils.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.joml.Matrix3x2fc;
import org.joml.Vector2d;
import org.jspecify.annotations.Nullable;

public record CustomQuadRenderState(
        Matrix3x2fc pose,
        Vector2d p1,
        Vector2d p2,
        Vector2d p3,
        Vector2d p4,
        int color,
        @Nullable ScreenRect scissorArea,
        @Nullable ScreenRect bounds
) implements SimpleGuiElementRenderState {
    public CustomQuadRenderState(
            Matrix3x2fc pose,
            Vector2d p1,
            Vector2d p2,
            Vector2d p3,
            Vector2d p4,
            int color,
            @Nullable ScreenRect scissorArea
    ) {
        this(pose, p1, p2, p3, p4, color, scissorArea, createBounds(p1, p2, p3, p4, pose, scissorArea));
    }

    @Override
    public void setupVertices(VertexConsumer vertices) {
        vertices.vertex(this.pose(), (float) this.p1.x, (float) this.p1.y).color(color);
        vertices.vertex(this.pose(), (float) this.p2.x, (float) this.p2.y).color(color);
        vertices.vertex(this.pose(), (float) this.p3.x, (float) this.p3.y).color(color);
        vertices.vertex(this.pose(), (float) this.p4.x, (float) this.p4.y).color(color);
    }

    @Override
    public RenderPipeline pipeline() {
        return RenderPipelines.GUI;
    }

    @Override
    public TextureSetup textureSetup() {
        return TextureSetup.empty();
    }

    private static @Nullable ScreenRect createBounds(
            Vector2d p1,
            Vector2d p2,
            Vector2d p3,
            Vector2d p4,
            Matrix3x2fc pose,
            @Nullable ScreenRect scissorArea
    ) {
        // Find the bounding box of the quad
        double minX = Math.min(Math.min(p1.x, p2.x), Math.min(p3.x, p4.x));
        double maxX = Math.max(Math.max(p1.x, p2.x), Math.max(p3.x, p4.x));
        double minY = Math.min(Math.min(p1.y, p2.y), Math.min(p3.y, p4.y));
        double maxY = Math.max(Math.max(p1.y, p2.y), Math.max(p3.y, p4.y));

        int x = (int) Math.floor(minX);
        int y = (int) Math.floor(minY);
        int width = (int) Math.ceil(maxX - minX);
        int height = (int) Math.ceil(maxY - minY);

        // Create screen rect from bounds
        ScreenRect screenRect = new ScreenRect(x, y, width, height);

        // Transform by pose matrix (if needed)
        screenRect = screenRect.transformEachVertex(pose);

        // Apply scissor area if present
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
}
