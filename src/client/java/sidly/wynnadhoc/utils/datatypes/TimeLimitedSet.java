package sidly.wynnadhoc.utils.datatypes;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class TimeLimitedSet<T> extends TimeLimitedMap<T, Boolean> {
    public TimeLimitedSet(long duration, TimeUnit unit) {
        super(duration, unit);
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
