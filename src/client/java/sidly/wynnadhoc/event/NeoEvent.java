package sidly.wynnadhoc.event;


import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class NeoEvent extends Event {
    private static final Map<Class<? extends Event>, List<Consumer<?>>> listeners = new HashMap<>();

    // Register a listener for a specific event class
    public static <T extends Event> void register(Class<T> eventClass, Consumer<T> listener) {
        listeners.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(listener);
    }

    // Post an event to all registered listeners
    @SuppressWarnings("unchecked")
    public static void post(Event event) {
        List<Consumer<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners == null) return;

        for (Consumer<?> listener : eventListeners) {
            ((Consumer<Event>) listener).accept(event);
        }
    }
}
