package sidly.wynnadhoc.wapi.item;

import com.google.gson.*;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Type;

public record IdentificationData(int min, int raw, int max) {
    @Override
    public @NonNull String toString() {
        return "[" + min + ", " + raw + ", " + min + "]";
    }

    public static JsonDeserializer<IdentificationData> getTypeAdaptor() {
        return (JsonElement json, Type typeOfT, JsonDeserializationContext ctx) -> {
            int min;
            int raw;
            int max;

            if (json.isJsonObject()) {
                JsonObject obj = json.getAsJsonObject();
                min = (obj.has("min") ? obj.get("min").getAsInt() : 0);
                max = (obj.has("max") ? obj.get("max").getAsInt() : 0);
                raw = (obj.has("raw") ? obj.get("raw").getAsInt() : 0);
            } else if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
                int value = json.getAsInt();
                min = value;
                raw = value;
                max = value;
            } else {
                throw new JsonParseException("Unexpected JSON for IdentificationData: " + json);
            }
            return new IdentificationData(min, raw, max);
        };
    }

    public boolean isStatic() {
        return min == max;
    }

    public float getPercent(int p) {
        return min + ((max - min) * (p / 100f));
    }

    public int getAverage() {
        return ((max - min) / 2) + min;
    }
}
