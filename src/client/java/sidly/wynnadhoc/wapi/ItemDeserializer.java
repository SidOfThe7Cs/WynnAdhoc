package sidly.wynnadhoc.wapi;

import com.google.gson.*;
import sidly.wynnadhoc.wapi.item.WynnItem;

import java.lang.reflect.Type;
import java.util.Set;

public class ItemDeserializer implements JsonDeserializer<WynnItem> {
    private final Gson delegate;

    public ItemDeserializer(Gson delegate) {
        this.delegate = delegate;
    }

    @Override
    public WynnItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!json.isJsonObject()) {
            System.err.println("Expected JSON object for Item, got: " + json);
            return null; // fallback
        }
        JsonObject obj = json.getAsJsonObject();

        try {

            // collect all classes that have custom adapters
            Set<Class<?>> customAdapters = Set.of(
            );

            // after deserializing
            WynnItem wynnItem = delegate.fromJson(obj, WynnItem.class);
            ApiUtils.fieldTracker.detectUnknownFields(obj, WynnItem.class, "root", customAdapters);
            return wynnItem;

        } catch (JsonSyntaxException e) {
            System.err.println("Failed to parse JSON: " + e.getMessage());
            Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
            String prettyJson = gsonPretty.toJson(obj);
            System.err.println("Offending JSON snippet:\n" + prettyJson);

            throw e;
        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() ||
                type == String.class ||
                Number.class.isAssignableFrom(type) ||
                type == Boolean.class ||
                type == Character.class;
    }

}
