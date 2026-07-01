package sidly.wynnadhoc.features.chests;

import com.google.gson.*;
import net.minecraft.util.math.BlockPos;

import java.util.Base64;

public record LootChest(int x, int y, int z, int tier, byte[] chestData) {
    public BlockPos getPos() {
        return new BlockPos(x, y, z);
    }

    // custom deserializer since spring automatically encodes to base64 but gson does not
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LootChest.class,
                    (JsonDeserializer<LootChest>) (json, typeOfT, context) -> {
                        JsonObject obj = json.getAsJsonObject();
                        int x = obj.get("x").getAsInt();
                        int y = obj.get("y").getAsInt();
                        int z = obj.get("z").getAsInt();
                        int tier = obj.get("tier").getAsInt();
                        byte[] byteData = new byte[0];
                        JsonElement chestDataElement = obj.get("chestData");
                        if (chestDataElement != null) {
                            if (chestDataElement.isJsonArray()) {
                                JsonArray array = chestDataElement.getAsJsonArray();
                                byteData = new byte[array.size()];
                                for (int i = 0; i < array.size(); i++) {
                                    byteData[i] = array.get(i).getAsByte();
                                }
                            } else if (chestDataElement.isJsonPrimitive()) {
                                byteData = Base64.getDecoder().decode(chestDataElement.getAsString());
                            }
                        }
                        return new LootChest(x, y, z, tier, byteData);
                    }
            ).create();
}
