package sidly.wynnadhoc.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.event.ClientTickEvent;
import sidly.wynnadhoc.utils.ItemUtils;

import java.util.List;

public class BowSpammer {
    private static final int ticksBetweenUses = 5; // 4.65 rounds to 5 ticks
    private static int useCooldown = 0;

    public static void onClientTick(ClientTickEvent event) {
        MinecraftClient client = event.client;
        if (client.player == null || client.world == null) return;

        if (!holdingItem() || !ConfigManager.INSTANCE.config.toggles.bowSpammerToggle) return;

        if (client.options.useKey.isPressed()) {
            // right-click is held
            if (useCooldown <= 0) {
                // Send use item packet
                assert client.interactionManager != null;
                client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
                useCooldown = ticksBetweenUses;
            }
            useCooldown--;
        }
    }

    // TODO dont cancel normal clicks (MinecraftClientMixin) and make work for all bows
    public static boolean holdingItem() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return false;
        ItemStack held = client.player.getMainHandStack();

        List<Text> tooltip = ItemUtils.getTooltip(held);
        if (!tooltip.isEmpty()) {
            return tooltip.getFirst().getString().toLowerCase().contains("epoch");
        }
        return false;
    }
}
