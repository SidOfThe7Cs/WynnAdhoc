package sidly.wynnadhoc.features.chests;

import net.minecraft.item.ItemStack;
import sidly.wynnadhoc.utils.BitUtils;

import java.util.ArrayList;
import java.util.List;

public interface EncodableItem {
    static EncodableItem decode(char encoded) {
        return switch (BitUtils.getBits(encoded, 0, 2)) {
            case 0 -> BoxItem.decode(encoded);
            case 1 -> IngredientItem.decode(encoded);
            default -> null;
        };
    }

    static EncodableItem fromItem(ItemStack itemStack) {
        if (itemStack == null) return null;
        BoxItem boxItem = BoxItem.fromItem(itemStack);
        if (boxItem != null) return boxItem;
        IngredientItem ingItem = IngredientItem.fromItem(itemStack);
        return ingItem;
    }

    static Character encodeItem(ItemStack itemStack) {
        EncodableItem encodableItem = fromItem(itemStack);
        if (encodableItem == null) return null;
        return encodableItem.encode();
    }

    static List<EncodableItem> fromByteArray(byte[] bytes) {
        if (bytes == null || bytes.length % 2 != 0) return List.of();
        List<EncodableItem> items = new ArrayList<>();
        for (int i = 0; i < bytes.length - 1; i += 2) {
            // Combine two bytes into a char (big-endian)
            char c = (char) ((bytes[i] << 8) | (bytes[i + 1] & 0xFF));
            items.add(EncodableItem.decode(c));
        }
        return items;
    }

    static byte[] toByteArray(List<EncodableItem> items) {
        byte[] bytes = new byte[items.size() * 2];
        int index = 0;
        for (EncodableItem item : items) {
            char c = item.encode();
            // Split char into two bytes (big-endian)
            bytes[index++] = (byte) (c >> 8);     // High byte
            bytes[index++] = (byte) (c & 0xFF);    // Low byte
        }
        return bytes;
    }

    char encode();
}
