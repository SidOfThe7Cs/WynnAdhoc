package sidly.wynnadhoc.utils.auto;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ItemStackHash;
import sidly.wynnadhoc.mixin.client.accessors.HandledScreenAccessor;

public class AutoUtils {

    public static void shiftClickSlot(HandledScreen<?> screen, int slotIndex) {
        if (screen == null) return;

        MinecraftClient client = MinecraftClient.getInstance();
        ScreenHandler handler = screen.getScreenHandler();
        Slot slot = handler.getSlot(slotIndex);
        if (slot == null || slot.getStack().isEmpty()) return;

        double guiScale = client.getWindow().getScaleFactor();
        int guiLeft = ((HandledScreenAccessor) screen).getX();
        int guiTop = ((HandledScreenAccessor) screen).getY();

        // Convert GUI-relative coordinates to window coordinates
        double targetX = (guiLeft + slot.x + 8) * guiScale;
        double targetY = (guiTop + slot.y + 8) * guiScale;

        MouseLerper.queueLerp(client, targetX, targetY, 125, () -> {
            ClientPlayerInteractionManager interactionManager = client.interactionManager;
            if (interactionManager != null) {
                interactionManager.clickSlot(screen.getScreenHandler().syncId, slot.id, 0, SlotActionType.QUICK_MOVE, client.player);
            }
        });
    }


    public static void closeScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.player != null) {
            client.player.closeHandledScreen();
        }
    }

    private static void sendShiftClickSlotPacket(HandledScreen<?> screen, int slotIndex) {
        if (screen == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        Slot slot = screen.getScreenHandler().getSlot(slotIndex);
        if (slot == null || slot.getStack().isEmpty()) return;

        ClientPlayerEntity player = client.player;

        int syncId = screen.getScreenHandler().syncId;
        int revision = screen.getScreenHandler().getRevision();
        short slotId = (short) slot.getIndex();
        byte button = 0; // left click
        SlotActionType actionType = SlotActionType.QUICK_MOVE; // shift-click
        ItemStackHash stack = ItemStackHash.EMPTY;

        // For modifiedStacks, you can usually send an empty unmodifiable map
        Int2ObjectMap<ItemStackHash> modifiedStacks = Int2ObjectMaps.emptyMap();

        ClickSlotC2SPacket packet = new ClickSlotC2SPacket(
                syncId,
                revision,
                slotId,
                button,
                actionType,
                modifiedStacks,
                stack
        );

        // Send packet to server
        player.networkHandler.sendPacket(packet);
    }
}
