package sidly.wynnadhoc.utils;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LocationUtils {
    public static boolean isInOuterVoid(double x, double z) {
        // Define the bounding box coordinates
        int minX = 13577;
        int maxX = 14046;
        int minZ = -3588;
        int maxZ = -3187;

        // Check if player is within the bounds
        return x >= minX && x <= maxX &&
                z >= minZ && z <= maxZ;
    }

    public static BlockPos getBlockUnderVec3d(Vec3d vec3d) {
        int x = (int) Math.floor(vec3d.x);
        int y = (int) Math.floor(vec3d.y);
        int z = (int) Math.floor(vec3d.z);

        return new BlockPos(x, y - 1, z); // block under the entity
    }
}
