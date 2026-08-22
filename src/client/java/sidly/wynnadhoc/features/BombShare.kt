package sidly.wynnadhoc.features;

import com.wynntils.core.text.StyledText;
import com.wynntils.core.text.StyledTextPart;
import com.wynntils.core.text.type.StyleType;
import com.wynntils.utils.type.IterationDecision;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.event.ChatMessageEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BombShare {
    private static final Set<String> prof = Set.of("prof", "profs", "speed bomb", "gxp");
    private static final Set<String> xp = Set.of("xp bomb", "dxp", "cxp", "txp", "double xp");
    private static final Set<String> chest = Set.of("loot chest", "dcl", "chest loot", "cloot");
    private static final Set<String> loot = Set.of("dloot", "loot bomb");
    private static final Map<String, ClickEvent> clickEventsToAdd = new HashMap<>();

    static {
        prof.forEach(s -> clickEventsToAdd.put(s, new ClickEvent.RunCommand("p prof share")));
        xp.forEach(s -> clickEventsToAdd.put(s, new ClickEvent.RunCommand("p xp share")));
        chest.forEach(s -> clickEventsToAdd.put(s, new ClickEvent.RunCommand("p chest share")));
        loot.forEach(s -> clickEventsToAdd.put(s, new ClickEvent.RunCommand("p loot share")));
    }

    public static void onChat(ChatMessageEvent event) {
        event.styledText = addClickEvents(event.styledText, clickEventsToAdd);
    }

    public static StyledText addClickEvents(StyledText styledText,
                                            Map<String, ClickEvent> wordClickMap) {
        if (wordClickMap == null || wordClickMap.isEmpty()) {
            return styledText;
        }

        // Build case-insensitive pattern
        String patternString = wordClickMap.keySet().stream()
                .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                .map(Pattern::quote)
                .collect(Collectors.joining("|"));

        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);

        return styledText.iterate((part, functionParts) -> {
            String partText = part.getString(null, StyleType.NONE);
            Matcher matcher = pattern.matcher(partText);

            List<StyledTextPart> newParts = new ArrayList<>();
            int lastEnd = 0;
            boolean foundMatch = false;

            while (matcher.find()) {
                foundMatch = true;
                String matchedWord = matcher.group();
                // Find the original word in the map (case-insensitive)
                String originalWord = wordClickMap.keySet().stream()
                        .filter(word -> word.equalsIgnoreCase(matchedWord))
                        .findFirst()
                        .orElse(matchedWord);

                WynnAdhocClient.LOGGER.temp("found match: " + originalWord);

                ClickEvent clickEvent = wordClickMap.get(originalWord);

                if (matcher.start() > lastEnd) {
                    String beforeText = partText.substring(lastEnd, matcher.start());
                    newParts.add(new StyledTextPart(
                            beforeText,
                            part.getPartStyle().getStyle(),
                            null,
                            Style.EMPTY
                    ));
                }

                // Use the actual matched text (preserving case) but with click event
                Style originalStyle = part.getPartStyle().getStyle();
                Style newStyle = originalStyle.withClickEvent(clickEvent);
                newParts.add(new StyledTextPart(
                        matchedWord, // Use matched text to preserve original case
                        newStyle,
                        null,
                        Style.EMPTY
                ));

                lastEnd = matcher.end();
            }

            if (foundMatch) {
                if (lastEnd < partText.length()) {
                    String afterText = partText.substring(lastEnd);
                    newParts.add(new StyledTextPart(
                            afterText,
                            part.getPartStyle().getStyle(),
                            null,
                            Style.EMPTY
                    ));
                }

                functionParts.clear();
                functionParts.addAll(newParts);
            }

            return IterationDecision.CONTINUE;
        });
    }
}
