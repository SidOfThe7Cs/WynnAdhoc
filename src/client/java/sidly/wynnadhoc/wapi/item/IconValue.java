package sidly.wynnadhoc.wapi.item;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IconValue {

    private final String id;
    private final String name;
    private final Object customModelData;
    private final String select;
    private final Set<Integer> rangeDispatch;

    public IconValue(String s) {
        this.id = s;
        this.name = s;
        this.select = "";
        this.rangeDispatch = new HashSet<>();
        this.customModelData = null;
    }

    public IconValue(String id, String name, String select, Set<Integer> rangeDispatch, Object customModelData) {
        this.id = id;
        this.name = name;
        this.select = select;
        this.rangeDispatch = rangeDispatch;
        this.customModelData = customModelData;
    }

    public static JsonDeserializer<IconValue> getTypeAdaptor() {
        return (JsonElement json, Type typeOfT, JsonDeserializationContext ctx) -> {
            if (json.isJsonPrimitive()) return new IconValue(json.getAsString());
            JsonObject obj = json.getAsJsonObject();
            JsonElement modelDataObj = obj.get("customModelData");

            Object result;
            if (modelDataObj.isJsonPrimitive()) result = modelDataObj.getAsInt();
            else result = modelDataObj.getAsJsonObject();

            List<JsonElement> select = (obj.get("select") != null) ? obj.get("select").getAsJsonArray().asList() : new ArrayList<>();

            Set<Integer> rangeDispatch = new HashSet<>();
            if (obj.get("rangeDispatch") != null) {
                for (JsonElement intE : obj.get("rangeDispatch").getAsJsonArray()) {
                    rangeDispatch.add(intE.getAsInt());
                }
            }

            return new IconValue(
                    obj.get("id").getAsString(),
                    obj.get("name").getAsString(),
                    select.isEmpty() ? "" : select.getFirst().getAsString(),
                    rangeDispatch,
                    result
            );
        };
    }

}
