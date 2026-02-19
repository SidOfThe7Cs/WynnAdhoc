package sidly.wynnadhoc.event;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

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
}
