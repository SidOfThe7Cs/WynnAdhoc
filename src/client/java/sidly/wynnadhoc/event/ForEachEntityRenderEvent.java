package sidly.wynnadhoc.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import sidly.wynnadhoc.utils.datatypes.TimeLimitedSet;

import java.util.concurrent.TimeUnit;

public class ForEachEntityRenderEvent extends Event<ForEachEntityRenderEvent> {
    private static final TimeLimitedSet<Integer> recentCache = new TimeLimitedSet<>(30, TimeUnit.MINUTES);

    public final Entity entity;
    public final Integer id;
    public final boolean isNew;
    public final WorldRenderEvent renderEvent;

    public ForEachEntityRenderEvent(Entity entity, WorldRenderEvent renderEvent) {
        this.entity = entity;
        this.renderEvent = renderEvent;
        this.id = entity.getId();
        this.isNew = !recentCache.contains(id);
        if (isNew) {
            recentCache.put(id);
        }
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
