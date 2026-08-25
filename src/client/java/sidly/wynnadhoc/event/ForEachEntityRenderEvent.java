package sidly.wynnadhoc.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public class ForEachEntityRenderEvent extends BaseForEachEntityEvent<ForEachEntityRenderEvent> {
    public final WorldRenderEvent renderEvent;

    public ForEachEntityRenderEvent(Entity entity, WorldRenderEvent renderEvent) {
        super(entity);
        this.renderEvent = renderEvent;
        this.fire();
    }

    public static void onRender(WorldRenderEvent event) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        ClientWorld world = client.world;
        if (world == null) return;
        Iterable<Entity> entities = world.getEntities();
        for (Entity e : entities) {
            new ForEachEntityRenderEvent(e, event);
        }
    }
}
