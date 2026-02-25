package sidly.wynnadhoc.features.lootruns.enums;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import sidly.wynnadhoc.features.lootruns.Camp;
import sidly.wynnadhoc.features.lootruns.LootrunCore;

public enum Camps {
    Canyon("Canyon of the Lost Excursion (South)", new Vec3d(578, 80, -5018)),
    Corkus("The Corkus Traversal", new Vec3d(-1555 ,98, -2668)),
    Molten("Molten Heights Hike", new Vec3d(1272 ,10, -5135)),
    Sky("Sky Islands Exploration", new Vec3d(1034 ,135, -4419)),
    SE("Silent Expanse Expedition", new Vec3d(991 ,78, -781));

    private final String displayName;
    private final Vec3d pos;

    public static Camps getClosestCamp(){
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return null;

        Camps closestCamp = null;
        double closestDist = Double.MAX_VALUE;
        Vec3d playerPos = client.player.getEntityPos();
        for (Camps camp : Camps.values()){
            double dist = camp.pos.squaredDistanceTo(playerPos);
            if (dist < closestDist){
                closestDist = dist;
                closestCamp = camp;
            }
        }
        return closestCamp;
    }

    public static boolean isNearAnyCamp(Vec3d pos, double range) {
        for (Camps camp : values()) {
            if (camp.pos.distanceTo(pos) <= range) {
                return true;
            }
        }
        return false;
    }

    public Camp getCamp() {
        return LootrunCore.INSTANCE.getCurrentLootrunData()
                .getCampData()
                .computeIfAbsent(this, key -> new Camp());
    }

    Camps(String displayName, Vec3d pos) {
        this.displayName = displayName;
        this.pos = pos;
    }
}
