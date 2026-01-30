package sidly.wynnadhoc.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Event<T extends Event<T>> {
    private static final List<ListenerEntry<?>> listeners = new ArrayList<>();

    // Registers a listener for a specific event class
    public static <E extends Event<E>> void register(Class<E> eventType, Consumer<E> listener) {
        listeners.add(new ListenerEntry<>(eventType, listener));
    }

    // Register a void listener (ignores the event object)
    public static <E extends Event<E>> void register(Class<E> eventType, Runnable listener) {
        listeners.add(new ListenerEntry<>(eventType, e -> listener.run()));
    }

    // Fires the event (calls all registered listeners)
    @SuppressWarnings("unchecked")
    protected void fire() {
        for (ListenerEntry<?> entry : listeners) {
            if (entry.eventType.isAssignableFrom(this.getClass())) {
                ((ListenerEntry<T>) entry).listener.accept((T) this);
            }
        }
    }

    // Helper record to store type-safe listener mappings
    private record ListenerEntry<E extends Event<E>>(Class<E> eventType, Consumer<E> listener) {}
}

