package sidly.wynnadhoc.event;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public class ForEachEntityEvent extends Event<ForEachEntityEvent> {
    public final Entity entity;

    public ForEachEntityEvent(Entity entity) {
        this.entity = entity;
    }

    public static void onClientTick(MinecraftClient client) {
        Iterable<Entity> entities = client.world.getEntities();
        for (Entity e : entities) {
            new ForEachEntityEvent(e).fire();
        }
    }
}
