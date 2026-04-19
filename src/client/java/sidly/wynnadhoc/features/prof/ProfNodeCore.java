package sidly.wynnadhoc.features.prof;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.event.TextDisplaySyncEvent;
import sidly.wynnadhoc.event.WorldRenderEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfNodeCore {
    private static final Pattern TIMER_PATTERN = Pattern.compile("§7(?<timeLeft>\\d+)s");
    private static final Pattern PROF_XP_GAIN_PATTERN = Pattern.compile("\\+(?<xpAmount>\\d+)\\D+(?<type>Woodcutting|Mining|Farming|Fishing) XP \\[(?<xpPercent>\\d+(?:\\.\\d+)?)%]");

    private static final Map<NodeType, Set<ProfNode>> nodesByType = new HashMap<>();
    private static final Map<Vec3d, ProfNode> nodesByLocation = new HashMap<>();
    private static final Set<Vec3d> subNodes = new HashSet<>();

    public static void nodeFound(NodeType type, Vec3d loc) {
        if (subNodes.contains(loc)) return;
        ProfNode node = new ProfNode(loc, type);
        nodesByType.computeIfAbsent(type, k -> new HashSet<>()).add(node);
        nodesByLocation.putIfAbsent(loc, node);
    }

    public static void onTimerSync(Vec3d loc, int timeLeft) {
        ProfNode node = nodesByLocation.get(loc);
        if (node != null) {
            node.syncTime(timeLeft);
        }
    }

    public static void onXpGain(Vec3d loc) {
        ProfNode node = nodesByLocation.get(loc);
        if (node != null) {
            node.syncTime(60); // TODO prof speed
        }
    }

    public static Set<ProfNode> getByType(NodeType type) {
        return nodesByType.get(type);
    }

    public static void onTextDisplaySync(TextDisplaySyncEvent event) {
        if (!ConfigManager.INSTANCE.config.toggles.customProfNodes) return;
        String name = event.string.lines().findFirst().orElse("").trim();
        NodeType type = NodeType.fromStr(name);
        if (type != NodeType.UNKNOWN) {
            nodeFound(type, event.pos);
            event.textDisplay.setText(Text.of(""));
            return;
        }

        Matcher timerMatcher = TIMER_PATTERN.matcher(event.string);
        if (timerMatcher.matches()) {
            int remaining = Integer.parseInt(timerMatcher.group("timeLeft"));
            onTimerSync(event.pos, remaining);

            Vec3d toDelete = null;
            for (ProfNode node : nodesByLocation.values()) {
                if (!node.isAt(event.pos) && node.getCurrentTimeLeft() == remaining) {
                    node.newSubNode(event.pos);
                    toDelete = event.pos;
                    subNodes.addAll(node.getPos());
                }
            }
            if (toDelete != null) {
                nodesByLocation.remove(toDelete);
                Vec3d finalToDelete = toDelete;
                nodesByType.values().stream()
                        .flatMap(Set::stream)
                        .filter(node -> node.isAt(finalToDelete))
                        .findFirst()
                        .ifPresent(nodeToRemove -> {
                            NodeType typeRemoving = nodeToRemove.getType();
                            Set<ProfNode> nodes = nodesByType.get(typeRemoving);
                            nodes.remove(nodeToRemove);
                        });
            }

            event.textDisplay.setText(Text.of(""));
            return;
        }

        Matcher xpGainmatcher = PROF_XP_GAIN_PATTERN.matcher(event.string); // TODO also has the gain item message
        if (xpGainmatcher.matches()) {
            onXpGain(event.pos);
            event.textDisplay.setText(Text.of(""));
        }

        // TODO handle % mined one font utils is done
    }

    public static void onRender(WorldRenderEvent event) {
        if (!ConfigManager.INSTANCE.config.toggles.customProfNodes) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        ClientWorld world = client.world;
        PlayerEntity player = client.player;
        if (world == null) return;
        if (player == null) return;
        Box box = player.getBoundingBox().expand(40);
        List<Entity> entities = world.getEntitiesByClass(Entity.class, box, (e) -> true);

        for (ProfNode node : nodesByLocation.values()) {
            node.render(event, entities, player);
        }
    }
}
