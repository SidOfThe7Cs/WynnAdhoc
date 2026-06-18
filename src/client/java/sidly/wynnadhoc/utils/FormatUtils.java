package sidly.wynnadhoc.utils;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FormatUtils {
    public static String millisToHMS(long millis) {
        long seconds = millis / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append("h");
        if (minutes > 0 || hours > 0) sb.append(minutes).append("m");
        sb.append(secs).append("s");

        return sb.toString();
    }

    public static long timeSinceIso(String isoTimestamp, ChronoUnit timeunit) {
        if (isoTimestamp == null || isoTimestamp.isEmpty()) return Long.MAX_VALUE;
        try {
            // Parse the ISO-8601 timestamp string into an Instant
            Instant past = Instant.parse(isoTimestamp);
            Instant now = Instant.ofEpochMilli(System.currentTimeMillis());

            if (timeunit == ChronoUnit.WEEKS) {
                return ChronoUnit.DAYS.between(past, now) / 7;
            }
            // Calculate days between past and now
            return timeunit.between(past, now);
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Return -1 if parsing failed
        }
    }

    public static String formatTime(long time, ChronoUnit unit) {
        double seconds = unit.getDuration().toMillis() / 1000.0 * Math.abs(time);
        return formatTime(seconds, ChronoUnit.SECONDS);
    }

    public static String formatTime(double time, ChronoUnit unit) {
        double seconds = unit.getDuration().toMillis() / 1000.0 * Math.abs(time);
        String base;

        if (seconds < 60) {
            base = String.format("%.1fs", seconds);
        } else if ((seconds /= 60.0) < 60) {
            base = String.format("%.1fm", seconds);
        } else if ((seconds /= 60.0) < 24) {
            base = String.format("%.1fh", seconds);
        } else if ((seconds /= 24.0) < 7) {
            base = String.format("%.1fd", seconds);
        } else if ((seconds /= 7.0) < 4.345) {
            base = String.format("%.1fw", seconds);
        } else if ((seconds *= 7.0 / 30.4375) < 12) {
            base = String.format("%.1fmo", seconds);
        } else {
            base = String.format("%.1fy", seconds / 12.0);
        }

        return base;
    }

    public static String getUnicode(String plain) {
        //DebugWindow.getInstance().log("getUnicode called for " + plain);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < plain.length(); i++) {
            char c = plain.charAt(i);

            // if it’s a private-use area symbol (usually used for custom fonts)
            //if (c >= '\uE000' && c <= '\uF8FF') {
            sb.append(String.format("\\u%04X", (int) c));
            //} else {
            //sb.append(c);
            //}
        }

        return sb.toString();
    }

    public static String[] splitByAnySpecialChar(String message) {
        return message.split("[^\\p{L}\\p{N} \\[\\]%+()]+");
        //split by anything that is not a letter number or space or [ or ] or % or +
    }

    public static String removeAllBut(String message, String toKeepRegex) {
        return message.replaceAll("[^" + toKeepRegex + "]", "");
    }

    public static String removeNonAscii(String message) {
        String cleaned = removeColorCodes(message);
        cleaned = cleaned.replaceAll("[^\\x00-\\x7F]", "");
        cleaned = cleaned.replace("\n", "");
        cleaned = cleaned.replace("  ", " ");
        return cleaned.trim();
    }

    public static String removeColorCodes(String message) {
        // Regular expression to match Minecraft color codes
        return message.replaceAll("§.", "");
    }

    public static int romanToInt(String roman) {
        Map<Character, Integer> map = Map.of(
                'I', 1, 'V', 5, 'X', 10
        );
        int result = 0;
        int prev = 0;
        for (int i = roman.length() - 1; i >= 0; i--) {
            int val = map.getOrDefault(roman.charAt(i), 0);
            if (val < prev) result -= val;
            else result += val;
            prev = val;
        }
        return result;
    }

    public static List<Text> splitTextLines(List<Text> input) {
        return input.stream().flatMap(l -> {
            String[] split = l.getString().split("\n");
            return Arrays.stream(split).map(Text::of);
        }).toList();
    }

    public static List<Text> wrapText(String text, int maxWidth, TextRenderer textRenderer) {
        List<Text> wrapped = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            if (textRenderer.getWidth(testLine) > maxWidth) {
                if (!currentLine.isEmpty()) {
                    wrapped.add(Text.literal(currentLine.toString()));
                    currentLine = new StringBuilder(word);
                } else {
                    // Word itself is too long, split it
                    wrapped.addAll(splitLongWord(word, maxWidth, textRenderer));
                    currentLine = new StringBuilder();
                }
            } else {
                if (!currentLine.isEmpty()) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }
        }

        if (!currentLine.isEmpty()) {
            wrapped.add(Text.literal(currentLine.toString()));
        }

        return wrapped;
    }

    private static List<Text> splitLongWord(String word, int maxWidth, TextRenderer textRenderer) {
        List<Text> parts = new ArrayList<>();
        StringBuilder currentPart = new StringBuilder();

        for (char c : word.toCharArray()) {
            String testPart = currentPart.toString() + c;
            if (textRenderer.getWidth(testPart) > maxWidth) {
                if (!currentPart.isEmpty()) {
                    parts.add(Text.literal(currentPart.toString()));
                    currentPart = new StringBuilder();
                }
            }
            currentPart.append(c);
        }

        if (!currentPart.isEmpty()) {
            parts.add(Text.literal(currentPart.toString()));
        }

        return parts;
    }

}
