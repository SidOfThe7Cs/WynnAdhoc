package sidly.wynnadhoc.wapi.item.enums;

import com.google.gson.JsonDeserializer;
import sidly.wynnadhoc.wapi.UnknownFieldTracker;

public class EnumUtils {
    public static <E extends Enum<E>> E safeValueOf(Class<E> enumClass, String value, UnknownFieldTracker tracker) {
        if (value == null) return null;

        String normalized = enumify(value);

        try {
            return Enum.valueOf(enumClass, normalized);
        } catch (IllegalArgumentException e) {
            tracker.missingEnum(enumClass, value, normalized);
            try {
                return Enum.valueOf(enumClass, "UNKNOWN");
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }
    }

    public static <E extends Enum<E>> JsonDeserializer<E> enumDeserializer(Class<E> enumClass, UnknownFieldTracker tracker) {
        return (json, type, ctx) -> EnumUtils.safeValueOf(enumClass, json.getAsString(), tracker);
    }

    public static String enumify(String s) {
        return s
                .trim()
                .replaceAll("[^\\x00-\\x7F]", "")  // remove non-ASCII
                .replace(" - ", "_")
                .replace(" ", "_")
                .replace("-", "_")
                .replace("1st", "First")
                .replace("2nd", "Second")
                .replace("3rd", "Third")
                .replace("4th", "Fourth")
                .replaceAll("([a-z])([A-Z])", "$1_$2") // camelCase -> SNAKE_CASE
                .replaceAll("\\?{10}", "TEN_QUESTION_MARKS")   // exactly 10
                .replaceAll("\\?{3}", "TRIPLE_QUESTION_MARK")  // exactly 3
                .replaceAll("[\\^'()]", "")
                .toUpperCase();
    }

}

