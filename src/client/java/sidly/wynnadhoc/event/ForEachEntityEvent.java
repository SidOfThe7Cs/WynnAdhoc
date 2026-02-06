package sidly.wynnadhoc.event;

import net.minecraft.entity.Entity;

public class ForEachEntityEvent extends Event<ForEachEntityEvent> {
    public final Entity entity;

    public ForEachEntityEvent(Entity entity) {
        this.entity = entity;
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
