package sidly.wynnadhoc.event.entity;

import net.minecraft.entity.Entity;
import sidly.wynnadhoc.event.ClientTickEvent;

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
