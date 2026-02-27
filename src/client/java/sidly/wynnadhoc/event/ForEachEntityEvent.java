package sidly.wynnadhoc.event;

import net.minecraft.entity.Entity;
import sidly.wynnadhoc.utils.datatypes.TimeLimitedSet;

import java.util.concurrent.TimeUnit;

public class ForEachEntityEvent extends Event<ForEachEntityEvent> {
    private static final TimeLimitedSet<Integer> recentCache = new TimeLimitedSet<>(30, TimeUnit.MINUTES);

    public final Entity entity;
    public final Integer id;
    public final boolean isNew;

    public ForEachEntityEvent(Entity entity) {
        this.entity = entity;
        this.id = entity.getId();
        this.isNew = !recentCache.contains(id);
        if (isNew) {
            recentCache.put(id);
        }
        this.fire();
    }

    public static void onClientTick(ClientTickEvent event) {
        if (event.client.world == null) return;
        Iterable<Entity> entities = event.client.world.getEntities();
        for (Entity e : entities) {
            new ForEachEntityEvent(e);
        }
    }
}
