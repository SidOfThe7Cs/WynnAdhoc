package sidly.wynnadhoc.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import sidly.wynnadhoc.WynnAdhocClient;

import java.util.Optional;

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

    public static boolean isInEndBiome() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null && client.player != null) {
            BlockPos playerPos = client.player.getBlockPos();
            RegistryEntry<Biome> biomeEntry = client.world.getBiome(playerPos);
            Optional<Registry<Biome>> optionalRegistry = client.world.getRegistryManager().getOptional(RegistryKeys.BIOME);

            if (optionalRegistry.isPresent()) {
                Registry<Biome> biomeRegistry = optionalRegistry.get();
                Identifier biomeId = biomeRegistry.getId(biomeEntry.value());
                if (biomeId != null) return biomeId.equals(Identifier.of("minecraft", "the_end"));
                else WynnAdhocClient.LOGGER.warn("Biome ID not found.");
            } else WynnAdhocClient.LOGGER.error("Biome registry not available.");
        }
        return false;
    }

    public enum RaidRoom {
        NOL_FIRST_PARASITES(new Vec3d(11351, 132, 1987), new Vec3d(11262, 40, 2087)),
        NOL_SECOND_CLOUDS(new Vec3d(11751, 33, 1512), new Vec3d(11607, 143, 1660)),
        NOL_SECOND_GATHERING(new Vec3d(11116, 81, 1125), new Vec3d(10765, -9, 1360)),
        NOL_THIRD_MAZE(new Vec3d(11758, 24, 2578), new Vec3d(11880, 105, 2481)),
        NOL_THIRD_TOWER(new Vec3d(11345, 186, 1556), new Vec3d(11287, 23, 1609)),
        NOL_BOSS(new Vec3d(11402, 162, 2377), new Vec3d(11594, 21, 2568));

        private final Vec3d min;
        private final Vec3d max;

        RaidRoom(Vec3d corner1, Vec3d corner2) {
            // Calculate min and max bounds for easier collision detection
            this.min = new Vec3d(
                    Math.min(corner1.x, corner2.x),
                    Math.min(corner1.y, corner2.y),
                    Math.min(corner1.z, corner2.z)
            );
            this.max = new Vec3d(
                    Math.max(corner1.x, corner2.x),
                    Math.max(corner1.y, corner2.y),
                    Math.max(corner1.z, corner2.z)
            );
        }

        /**
         * Checks if a location is within this raid room's bounds
         */
        public boolean contains(Vec3d loc) {
            return loc.x >= min.x && loc.x <= max.x &&
                    loc.y >= min.y && loc.y <= max.y &&
                    loc.z >= min.z && loc.z <= max.z;
        }

        /**
         * Gets which raid room a location belongs to
         *
         * @param loc The location to check
         * @return The RaidRoom containing the location, or null if not in any room
         */
        public static RaidRoom getRaidRoom(Vec3d loc) {
            for (RaidRoom room : values()) {
                if (room.contains(loc)) {
                    return room;
                }
            }
            return null;
        }

        public Vec3d getMin() {
            return min;
        }

        public Vec3d getMax() {
            return max;
        }
    }
}
