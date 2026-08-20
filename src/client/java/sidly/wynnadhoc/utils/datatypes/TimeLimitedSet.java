package sidly.wynnadhoc.utils.datatypes;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class TimeLimitedSet<T> {
    private final Cache<@NotNull T, @NotNull Boolean> cache;

    public TimeLimitedSet(long duration, TimeUnit unit) {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(duration, unit)
                .build();
    }

    public void put(T element) {
        cache.put(element, Boolean.TRUE);
    }

    public boolean contains(T element) {
        return cache.getIfPresent(element) != null;
    }

    public Set<T> elements() {
        return cache.asMap().keySet();
    }

    public Stream<T> stream() {
        return elements().stream();
    }
}
