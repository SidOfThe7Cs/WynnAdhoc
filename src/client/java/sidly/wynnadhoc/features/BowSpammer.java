package sidly.wynnadhoc.features;

import com.wynntils.utils.mc.MouseUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.neoforged.bus.api.ICancellableEvent;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.event.ClientTickEvent;
import sidly.wynnadhoc.utils.ItemUtils;

import java.util.List;

public class BowSpammer {
    private static int useCooldown = 0;

    public static void onClientTick(ClientTickEvent event) {
        MinecraftClient client = event.client;
        if (client.player == null || client.world == null) return;
        if (!isOn()) return;

        if (client.options.useKey.isPressed()) {
            // right-click is held
            if (useCooldown <= 0) {
                // Send use item packet
                assert client.interactionManager != null;
                MouseUtils.sendRightClickInput();
                useCooldown = ConfigManager.INSTANCE.config.toggles.bowSpammerToggle;
            }
            useCooldown--;
        }
    }

    public static boolean isOn() {
        return holdingItem() && ConfigManager.INSTANCE.config.toggles.bowSpammerToggle != 0;
    }

    public static void onUseItem(ICancellableEvent event) {
        if (BowSpammer.isOn()) {
            event.setCanceled(true);
        }
    }

    // TODO dont cancel normal clicks (MinecraftClientMixin) and make work for all bows
    public static boolean holdingItem() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return false;
        ItemStack held = client.player.getMainHandStack();

        List<Text> tooltip = ItemUtils.getTooltip(held);
        if (!tooltip.isEmpty()) {
            String name = tooltip.getFirst().getString().toLowerCase();
            return name.contains("epoch");
        }
        return false;
    }
}
