package sidly.wynnadhoc.wapi.item;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import sidly.wynnadhoc.wapi.item.enums.DropMetaEvent;
import sidly.wynnadhoc.wapi.item.enums.DropMetaName;
import sidly.wynnadhoc.wapi.item.enums.DropMetaType;

import java.util.HashSet;
import java.util.Set;

/**
 * @param coordinates [x, y, z] or [x, y, z, radius?] // TODO coords
 */
public record DropMeta(DropMetaName name, Set<DropMetaType> type, DropMetaEvent event, Set<Coords> coordinates) {
    public static JsonDeserializer<Set<DropMetaType>> getTypeAdaptor() {
        return (json, typeOfT, context) -> {
            Set<DropMetaType> result = new HashSet<>();

            if (json.isJsonArray()) {
                for (JsonElement element : json.getAsJsonArray()) {
                    result.add(context.deserialize(element, DropMetaType.class));
                }
            } else {
                // Single string → wrap into Set
                result.add(context.deserialize(json, DropMetaType.class));
            }

            return result;
        };
    }
}
