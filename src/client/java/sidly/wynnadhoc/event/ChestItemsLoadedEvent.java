package sidly.wynnadhoc.event;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;

public class ChestItemsLoadedEvent extends Event<ChestItemsLoadedEvent> {
    public final GenericContainerScreen containerScreen;
    public List<ItemStack> getItems() {
        int containerSlots = containerScreen.getScreenHandler().slots.size() - 36; // inventory is 36 size

        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < containerSlots; i++) {
            items.add(containerScreen.getScreenHandler().getSlot(i).getStack());
        }

        return items;
    }

    public ChestItemsLoadedEvent(GenericContainerScreen containerScreen) {
        this.containerScreen = containerScreen;
        this.fire();
    }

    private static boolean itemsLoaded = false;

    public static void onScreenRender(ScreenRenderEvent event) {
        if (!(event.screen instanceof GenericContainerScreen container)) return;
        if (itemsLoaded) return; // already triggered

        boolean hasNonAir = false;
        int containerSlots = container.getScreenHandler().slots.size() - 36; // inventory is 36 size
        for (int i = 0; i < containerSlots; i++) {
            Slot slot = container.getScreenHandler().getSlot(i);
            if (!slot.getStack().isEmpty()) {
                hasNonAir = true;
                break;
            }
        }

        if (hasNonAir) {
            itemsLoaded = true;
            new ChestItemsLoadedEvent(container);
        }
    }

    public static void onScreenOpened() {
        itemsLoaded = false;
    }

}
