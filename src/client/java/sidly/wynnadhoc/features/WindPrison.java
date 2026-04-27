package sidly.wynnadhoc.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.event.TextDisplaySyncEvent;
import sidly.wynnadhoc.event.WorldRenderEvent;
import sidly.wynnadhoc.utils.render.RenderUtilsKt;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WindPrison {
    public static Pattern PATTERN = Pattern.compile("§c❋ §7(?<timeLeft>\\d+)");

    private static final Map<Entity, Pair<Box, Double>> cache = new HashMap<>();

    public static void onTextDisplaySync(TextDisplaySyncEvent event) {
        cache.put(event.textDisplay, null);
        Matcher matcher = PATTERN.matcher(event.string);
        if (matcher.find()) {
            try {
                double timeLeft = Integer.parseInt(matcher.group("timeLeft"));
                Entity vehicle = event.textDisplay.getVehicle();
                Box box = event.textDisplay.getBoundingBox().expand(2);
                if (vehicle != null) {
                    box = vehicle.getBoundingBox();
                }
                cache.put(event.textDisplay, new Pair<>(box, timeLeft));
            } catch (Exception e) {
                WynnAdhocClient.LOGGER.error("failure in wind prison detection: " + e.getMessage());
            }
        } else {
            cache.remove(event.textDisplay);
        }
    }

    public static void onWorldRender(WorldRenderEvent event) {
        if (MinecraftClient.getInstance().world == null) return;

        // Remove entities that no longer exist in the world
        List<Entity> toRemove = new ArrayList<>();
        for (Entity entity : cache.keySet()) {
            boolean found = false;
            for (Entity worldEntity : MinecraftClient.getInstance().world.getEntities()) {
                if (entity == worldEntity) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                toRemove.add(entity);
            }
        }
        for (Entity entity : toRemove) {
            cache.remove(entity);
        }

        for (Map.Entry<Entity, Pair<Box, Double>> entry : cache.entrySet()) {
            if (entry.getValue() == null) continue;
            RenderUtilsKt.drawBox(event, entry.getValue().getLeft(), Color.PINK, 1.f, false, true, 1.0);
            List<Text> test = new ArrayList<>();
            test.add(Text.literal(String.valueOf(entry.getValue().getRight())));
            event.drawText(entry.getValue().getLeft().getCenter(), test, 1.f, false);
        }
    }
}
