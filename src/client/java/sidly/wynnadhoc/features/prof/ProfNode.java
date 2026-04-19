package sidly.wynnadhoc.features.prof;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import sidly.wynnadhoc.event.WorldRenderEvent;

import java.util.ArrayList;
import java.util.List;

public class ProfNode {
    private final NodeType type;
    private final List<Vec3d> node = new ArrayList<>();
    private Vec3d activeNode;
    private int leftAtLastSync = 0;
    private Long lastSync = 0L;

    public ProfNode(Vec3d loc, NodeType type) {
        activeNode = loc;
        node.add(loc);
        this.type = type;
    }

    public void syncTime(int timeLeft) {
        this.leftAtLastSync = timeLeft;
        this.lastSync = System.currentTimeMillis();
    }

    public void newSubNode(Vec3d loc) {
        node.add(loc);
        activeNode = loc;
    }

    public boolean isAt(Vec3d loc) {
        return node.contains(loc);
    }

    public boolean isAt(List<Vec3d> loc) {
        return node.stream().anyMatch(loc::contains);
    }

    public NodeType getType() {
        return type;
    }

    public Vec3d getClosest(Vec3d playerPos) {
        Vec3d closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Vec3d n : node) {
            double distSQ = playerPos.squaredDistanceTo(n);
            if (distSQ < closestDist) {
                closestDist = distSQ;
                closest = n;
            }
        }
        return closest;
    }

    public int getCurrentTimeLeft() {
        int timeDif = (int) (System.currentTimeMillis() - lastSync) / 1000;
        return lastSync == 0L ? 0 : Math.max(0, leftAtLastSync - timeDif);
    }

    private Formatting getTimeColor(long seconds) {
        if (seconds <= 0) return Formatting.GREEN;
        if (seconds <= 15) return Formatting.YELLOW;
        return Formatting.RED;
    }

    public void render(WorldRenderEvent event, List<Entity> entities, PlayerEntity player) {
        List<Text> text = new ArrayList<>();
        int timeLeft = getCurrentTimeLeft();
        text.add(Text.literal(type.name()));
        text.add(Text.literal(String.valueOf(timeLeft)).formatted(getTimeColor(timeLeft)));
        if (node.size() == 1) {
            Vec3d n = node.getFirst();
            double dist = MinecraftClient.getInstance().player.getEntityPos().squaredDistanceTo(n);
            if (dist < 90) {
                boolean exists = entities.stream().anyMatch(e -> e.getEntityPos().equals(n));
                if (!exists) return;
            }
        }
        event.drawText(getClosest(player.getEntityPos()), text, 0.6f, false);
    }

    public List<Vec3d> getPos() {
        return node;
    }
}
