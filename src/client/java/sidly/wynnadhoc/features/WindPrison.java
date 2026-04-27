package sidly.wynnadhoc.features;

import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.SimpleFeatureToggles;
import sidly.wynnadhoc.event.ForEachEntityRenderEvent;
import sidly.wynnadhoc.utils.render.RenderUtilsKt;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WindPrison {
    public static Pattern WIND_PRISON_PATTERN = Pattern.compile("§c❋ §7(?<timeLeft>\\d+)");
    public static Pattern MARKS_PATTERN = Pattern.compile("§c✜ §7(?<count>\\d+)");

    private static SimpleFeatureToggles config() {
        return ConfigManager.INSTANCE.config.toggles;
    }

    public static void onEntity(ForEachEntityRenderEvent event) {
        if (event.entity instanceof DisplayEntity.TextDisplayEntity textDisplay) {
            String string = textDisplay.getText().getString();

            List<Text> effects = new ArrayList<>();
            Box box = textDisplay.getVehicle() == null ? textDisplay.getBoundingBox().expand(2) : textDisplay.getVehicle().getBoundingBox();
            boolean drawBox = false;

            if (config().showWindPrisonBox) {
                Matcher windPrisonMatcher = WIND_PRISON_PATTERN.matcher(string);
                if (windPrisonMatcher.find()) {
                    try {
                        double timeLeft = Integer.parseInt(windPrisonMatcher.group("timeLeft"));
                        effects.add(Text.literal("WP: " + timeLeft + "s"));
                        drawBox = true;
                    } catch (Exception e) {
                        WynnAdhocClient.LOGGER.error("failure in wind prison detection: " + e.getMessage());
                    }
                }
            }

            if (config().showMarksCount) {
                Matcher marksMatcher = MARKS_PATTERN.matcher(string);
                if (marksMatcher.find()) {
                    try {
                        double marks = Integer.parseInt(marksMatcher.group("count"));
                        effects.add(Text.literal("Marks: " + marks).formatted(Formatting.RED));
                    } catch (Exception e) {
                        WynnAdhocClient.LOGGER.error("failure in marks detection: " + e.getMessage());
                    }
                }
            }

            if (drawBox) {
                RenderUtilsKt.drawBox(event.renderEvent, box, Color.PINK, 1.f, false, true, 1.0);
            }

            if (!effects.isEmpty()) event.renderEvent.drawText(box.getCenter(), effects, 1.f, false);
        }
    }
}
