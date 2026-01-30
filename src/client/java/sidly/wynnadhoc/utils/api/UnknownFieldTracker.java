package sidly.wynnadhoc.utils.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class UnknownFieldTracker {
    // path -> set of types
    private final Map<String, Set<String>> unknownFields = new HashMap<>();
    private final Map<String, Set<Object>> unknownFieldsValues = new HashMap<>();

    public Map<String, Set<String>> getUnknownFields() {
        return unknownFields;
    }

    public Map<String, Set<Object>> getUnknownFieldsValues() {
        return unknownFieldsValues;
    }

    void record(String path, JsonElement value) {
        unknownFields.computeIfAbsent(path, k -> new HashSet<>()).add(typeOf(value));
        unknownFieldsValues.computeIfAbsent(path, k -> new HashSet<>()).add(value);
    }

    // called with a JsonObject (.fromJson) the class of that object path should be "root" and a set of classes that have custom adapters
    public void detectUnknownFields(JsonObject json, Class<?> clazz, String path, Set<Class<?>> ignoreClasses) {
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String fieldName = entry.getKey();
            JsonElement value = entry.getValue();

            Field field = null;
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                // field not found in class
            }

            // If we found a field and it’s in the ignore list, skip entirely
            if (field != null && ignoreClasses.contains(field.getType())) {
                continue;
            }

            // If field is missing in class, AND it’s not ignored, print unknown
            if (field == null) {
                record(path + "." + fieldName, value);
                continue;
            }

            // Otherwise, recurse if it’s an object
            Class<?> fieldType = field.getType();

            if (Map.class.isAssignableFrom(fieldType)) {
                // Skip entirely if either the key or value type is ignored
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType pt) {
                    Class<?> keyClass = (Class<?>) pt.getActualTypeArguments()[0];
                    Class<?> valueClass = (Class<?>) pt.getActualTypeArguments()[1];
                    if (ignoreClasses.contains(keyClass) || ignoreClasses.contains(valueClass)) {
                        continue; // skip the whole map
                    }

                    // Optional: recurse into values if they are objects and not ignored
                    if (valueClass != null && !ignoreClasses.contains(valueClass)) {
                        for (JsonElement element : value.getAsJsonArray()) {
                            // this won't actually work directly for Map, you may want to skip maps entirely
                        }
                    }
                }
                continue; // skip map by default if you just want to ignore custom types
            }

            if (value.isJsonObject() && !fieldType.isPrimitive() && !fieldType.equals(String.class)
                    && !fieldType.isEnum() && !ignoreClasses.contains(fieldType)) {
                detectUnknownFields(value.getAsJsonObject(), fieldType, path + "." + fieldName, ignoreClasses);
            }

            // Handle lists of objects
            if (value.isJsonArray() && List.class.isAssignableFrom(fieldType)) {
                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType pt) {
                    Class<?> listClass = (Class<?>) pt.getActualTypeArguments()[0];
                    if (!ignoreClasses.contains(listClass)) {
                        for (JsonElement element : value.getAsJsonArray()) {
                            if (element.isJsonObject()) {
                                detectUnknownFields(element.getAsJsonObject(), listClass, path + "." + fieldName + "[]", ignoreClasses);
                            }
                        }
                    }
                }
            }
        }
    }

    private String typeOf(JsonElement elem) {
        if (elem.isJsonNull()) return "NULL";
        if (elem.isJsonObject()) return "OBJECT";
        if (elem.isJsonArray()) {
            Set<String> elementTypes = new HashSet<>();
            for (JsonElement child : elem.getAsJsonArray()) {
                elementTypes.add(typeOf(child));
            }
            return "ARRAY<" + String.join("|", elementTypes) + ">";
        }
        if (elem.isJsonPrimitive()) {
            JsonPrimitive p = elem.getAsJsonPrimitive();
            if (p.isBoolean()) return "BOOLEAN";
            if (p.isNumber()) return "NUMBER";
            if (p.isString()) return "STRING";
        }
        return "UNKNOWN";
    }


    void printReport() {
        unknownFields.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    var values = unknownFieldsValues.get(e.getKey());
                    String valuesStr;
                    if (values != null) {
                        if (values.size() < 100) {
                            valuesStr = values.toString();
                        } else {
                            valuesStr = "more than 100 values";
                        }
                    } else {
                        valuesStr = "[]";
                    }

                    System.err.println(
                            "Unknown field: " + e.getKey()
                                    + " | Types: " + e.getValue()
                                    + " | Values: " + valuesStr
                    );
                });

    }
}
