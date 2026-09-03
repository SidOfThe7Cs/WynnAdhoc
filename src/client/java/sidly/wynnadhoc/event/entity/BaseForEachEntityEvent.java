package sidly.wynnadhoc.event.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import org.jspecify.annotations.Nullable;
import sidly.wynnadhoc.event.ClientTickEvent;
import sidly.wynnadhoc.event.Event;
import sidly.wynnadhoc.utils.datatypes.TimeLimitedMap;
import sidly.wynnadhoc.utils.datatypes.TimeLimitedSet;
import sidly.wynnadhoc.utils.text.TextDisplayParser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class BaseForEachEntityEvent<T extends BaseForEachEntityEvent<T>> extends Event<T> {
    private static final TimeLimitedSet<Integer> recentCache = new TimeLimitedSet<>(10, TimeUnit.MINUTES);
    private static final TimeLimitedSet<Integer> detectedCache = new TimeLimitedSet<>(60, TimeUnit.SECONDS);
    private static final TimeLimitedMap<Integer, MobRenderData> renderCache = new TimeLimitedMap<>(7, TimeUnit.SECONDS);
    private static final Set<Integer> lastFrame = new HashSet<>();

    public final Entity entity;
    public final Integer id;
    public final boolean isNew;
    public final @Nullable TextDisplayParser textParser;

    public boolean isDetected() {
        return detectedCache.containsKeyAndRefresh(id);
    }

    public void markDetected() {
        detectedCache.put(id);
    }

    public void highlight(MobRenderData data) {
        renderCache.put(id, data);
    }

    public BaseForEachEntityEvent(Entity entity) {
        this.entity = entity;
        this.id = entity.getId();
        this.isNew = !recentCache.contains(id);
        lastFrame.add(id);

        if (entity instanceof DisplayEntity.TextDisplayEntity display) textParser = new TextDisplayParser(display);
        else textParser = null;

        if (isNew) {
            recentCache.put(id);
        }
    }

    public static void onTick(ClientTickEvent event) {
        lastFrame.clear();
    }

    public static Collection<MobRenderData> toRender() {
        return renderCache.entries().stream()
                .filter(e -> lastFrame.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .toList();
    }
}
