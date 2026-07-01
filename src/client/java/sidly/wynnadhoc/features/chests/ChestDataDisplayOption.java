package sidly.wynnadhoc.features.chests;

import com.wynntils.models.gear.type.GearTier;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public enum ChestDataDisplayOption {
    GLOBAL_ITEM_COUNT((ChestDataCache data) -> {
        Map<GearTier, Integer> map = data.globalItemCounts();
        if (map.isEmpty()) map = data.localItemCounts();
        Text text = Text.literal("\nGlobal Boxes:");
        map.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getKey().ordinal(), a.getKey().ordinal()))
                .forEach((entry) -> {
                    Formatting format = entry.getKey().getChatFormatting();
                    Text coloredText = Text.literal(" " + entry.getValue()).formatted(format);
                    text.getSiblings().add(coloredText);
                });
        return Collections.singletonList(text);
    }),
    LOCAL_ITEM_COUNT((ChestDataCache data) -> {
        Map<GearTier, Integer> map = data.localItemCounts();
        Text text = Text.literal("\nBoxes Found:");
        map.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getKey().ordinal(), a.getKey().ordinal()))
                .forEach((entry) -> {
                    Formatting format = entry.getKey().getChatFormatting();
                    Text coloredText = Text.literal(" " + entry.getValue()).formatted(format);
                    text.getSiblings().add(coloredText);
                });
        return Collections.singletonList(text);
    }),
    GLOBAL_ING_COUNT((ChestDataCache data) -> {
        Map<Integer, Integer> map = data.globalIngCounts();
        if (map.isEmpty()) map = data.localIngCounts();
        Text text = Text.literal("\nGlobal Ings:");
        map.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getKey(), a.getKey()))
                .forEach((entry) -> {
                    Formatting format = getIngFormatting(entry.getKey());
                    Text coloredText = Text.literal(" " + entry.getValue()).formatted(format);
                    text.getSiblings().add(coloredText);
                });
        return Collections.singletonList(text);
    }),
    LOCAL_ING_COUNT((ChestDataCache data) -> {
        Map<Integer, Integer> map = data.localIngCounts();
        Text text = Text.literal("\nIngs Found:");
        map.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getKey(), a.getKey()))
                .forEach((entry) -> {
                    Formatting format = getIngFormatting(entry.getKey());
                    Text coloredText = Text.literal(" " + entry.getValue()).formatted(format);
                    text.getSiblings().add(coloredText);
                });
        return Collections.singletonList(text);
    }),
    GLOBAL_ITEM_PERCENTS((ChestDataCache data) -> {
        Map<Integer, Integer> map = data.globalItemPercents();
        if (map.isEmpty()) map = data.localItemPercents();
        Text text = Text.literal("\nBoxes lvl:");
        map.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .forEach((entry) -> {
                    Text coloredText = Text.literal("\n" + entry.getKey() + "-" + (entry.getKey() + 4) + " " + entry.getValue() + "%");
                    text.getSiblings().add(coloredText);
                });
        return Collections.singletonList(text);
    }),
    LOCAL_ITEM_PERCENTS((ChestDataCache data) -> {
        Map<Integer, Integer> map = data.localItemPercents();
        Text text = Text.literal("\nBoxes lvl:");
        map.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .forEach((entry) -> {
                    Text coloredText = Text.literal("\n" + entry.getKey() + "-" + (entry.getKey() + 4) + " " + entry.getValue() + "%");
                    text.getSiblings().add(coloredText);
                });
        return Collections.singletonList(text);
    }),
    GLOBAL_ING_PERCENTS((ChestDataCache data) -> {
        Map<Integer, Integer> map = data.globalIngPercents();
        if (map.isEmpty()) map = data.localIngPercents();
        Text text = Text.literal("\nIngs lvl:");
        map.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .forEach((entry) -> {
                    Text coloredText = Text.literal("\n" + entry.getKey() + " " + entry.getValue() + "%");
                    text.getSiblings().add(coloredText);
                });
        return Collections.singletonList(text);
    }),
    LOCAL_ING_PERCENTS((ChestDataCache data) -> {
        Map<Integer, Integer> map = data.localIngPercents();
        Text text = Text.literal("\nIngs lvl:");
        map.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .forEach((entry) -> {
                    Text coloredText = Text.literal("\n" + entry.getKey() + " " + entry.getValue() + "%");
                    text.getSiblings().add(coloredText);
                });
        return Collections.singletonList(text);
    });

    private final Function<ChestDataCache, List<Text>> toText;

    public List<Text> toText(ChestDataCache data) {
        return toText.apply(data);
    }

    private static Formatting getIngFormatting(Integer tier) {
        return switch (tier) {
            case 0 -> Formatting.GRAY;
            case 1 -> Formatting.YELLOW;
            case 2 -> Formatting.LIGHT_PURPLE;
            case 3 -> Formatting.AQUA;
            default -> Formatting.WHITE;
        };
    }

    ChestDataDisplayOption(Function<ChestDataCache, List<Text>> toText) {
        this.toText = toText;
    }
}
