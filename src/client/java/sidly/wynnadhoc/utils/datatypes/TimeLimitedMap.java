package sidly.wynnadhoc.utils.datatypes;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimeLimitedMap<K, V> {
    protected final Cache<@NotNull K, @NotNull V> cache;

    public TimeLimitedMap(long duration, TimeUnit unit) {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(duration, unit)
                .build();
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public V get(K key) {
        return cache.getIfPresent(key);
    }

    public void remove(K key) {
        cache.invalidate(key);
    }

    public boolean containsKey(K key) {
        return cache.getIfPresent(key) != null;
    }

    public boolean containsKeyAndRefresh(K key) {
        V element = cache.getIfPresent(key);
        if (element != null) {
            cache.put(key, element);
            return true;
        }
        return false;
    }

    public Collection<Map.Entry<K, V>> entries() {
        return cache.asMap().entrySet();
    }

    public Collection<V> values() {
        return cache.asMap().values();
    }

    public Collection<K> keySet() {
        return cache.asMap().keySet();
    }

    public void clear() {
        cache.invalidateAll();
    }

    public boolean isEmpty() {
        cache.cleanUp();
        return keySet().isEmpty();
    }
}
