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
}
