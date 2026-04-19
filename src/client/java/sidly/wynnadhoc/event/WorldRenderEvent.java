package sidly.wynnadhoc.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.List;

public class WorldRenderEvent extends Event<WorldRenderEvent> {
    public MatrixStack matrices;
    public Camera camera;
    public VertexConsumerProvider.Immediate vertexConsumerProvider;
    public float partialTicks;

    public WorldRenderEvent(MatrixStack matrices, Camera camera, VertexConsumerProvider.Immediate vertexConsumerProvider, float partialTicks) {
        this.matrices = matrices;
        this.camera = camera;
        this.vertexConsumerProvider = vertexConsumerProvider;
        this.partialTicks = partialTicks;
        this.fire();
    }

    public void drawText(
            Vec3d location,
            List<Text> text,
            float scale,
            boolean depth
    ) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        Camera camera = client.gameRenderer.getCamera();

        Matrix4f matrix = new Matrix4f();
        Vec3d viewerPos = camera.getCameraPos();
        float adjustedScale = scale * 0.05f;

        matrix.translate(
                (float) (location.x - viewerPos.x),
                (float) (location.y - viewerPos.y),
                (float) (location.z - viewerPos.z)
        ).rotate(camera.getRotation()).scale(adjustedScale, -adjustedScale, adjustedScale);

        for (int i = 0; i < text.size(); i++) {
            textRenderer.draw(
                    text.get(i),
                    -textRenderer.getWidth(text.get(i)) / 2f,
                    i * textRenderer.fontHeight,
                    -1,
                    false,
                    matrix,
                    this.vertexConsumerProvider,
                    depth ? TextRenderer.TextLayerType.NORMAL : TextRenderer.TextLayerType.SEE_THROUGH,
                    0,
                    15728880
            );
        }
    }
}
