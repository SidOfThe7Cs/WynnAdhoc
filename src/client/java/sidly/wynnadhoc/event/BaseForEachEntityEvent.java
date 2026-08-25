package sidly.wynnadhoc.event;

import net.minecraft.entity.Entity;
import sidly.wynnadhoc.utils.datatypes.TimeLimitedSet;

import java.util.concurrent.TimeUnit;

public abstract class BaseForEachEntityEvent<T extends BaseForEachEntityEvent<T>> extends Event<T> {
    private static final TimeLimitedSet<Integer> recentCache = new TimeLimitedSet<>(30, TimeUnit.MINUTES);

    public final Entity entity;
    public final Integer id;
    public final boolean isNew;

    public BaseForEachEntityEvent(Entity entity) {
        this.entity = entity;
        this.id = entity.getId();
        this.isNew = !recentCache.contains(id);
        if (isNew) {
            recentCache.put(id);
        }
    }
}
