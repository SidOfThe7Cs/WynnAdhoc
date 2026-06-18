package sidly.wynnadhoc.wapi.item;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public record Coords(int x, int y, int z, int r) {
    public static JsonDeserializer<Set<Coords>> getTypeAdaptor() {
        return (JsonElement json, Type typeOfT, JsonDeserializationContext ctx) -> {
            Set<Coords> coordsSet = new HashSet<>();

            if (json.isJsonArray()) {
                JsonArray array = json.getAsJsonArray();
                if (array.get(0).isJsonPrimitive()) {
                    coordsSet.add(parseAsArray(array));
                } else {
                    for (int i = 0; i < array.size(); i++) {
                        coordsSet.add(parseAsArray(array.get(i).getAsJsonArray()));
                    }
                }
            } else if (!json.getAsBoolean()) {
                return null;
            } else {
                throw new JsonParseException("Expected JSON Array or Object for coordinates, got: " + json);
            }

            return coordsSet;
        };
    }

    private static Coords parseAsArray(JsonArray array) {
        if (array.size() == 3) {
            return new Coords(array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt(), 0);
        } else if (array.size() == 4) {
            return new Coords(array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt(), array.get(3).getAsInt());
        } else {
            throw new JsonParseException("Coords had unexpected array size: " + array);
        }
    }
}
