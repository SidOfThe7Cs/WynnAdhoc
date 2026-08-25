package sidly.wynnadhoc.event;

import net.minecraft.entity.Entity;

public class ForEachEntityEvent extends BaseForEachEntityEvent<ForEachEntityEvent> {
    public ForEachEntityEvent(Entity entity) {
        super(entity);
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
