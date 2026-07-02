package sidly.wynnadhoc.event;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.List;

public class ChestItemsLoadedEvent extends Event<ChestItemsLoadedEvent> {
    public final GenericContainerScreen containerScreen;
    public final List<ItemStack> items;

    private List<ItemStack> initItems() {
        int containerSlots = containerScreen.getScreenHandler().slots.size() - 36; // inventory is 36 size

        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < containerSlots; i++) {
            ItemStack stack = containerScreen.getScreenHandler().getSlot(i).getStack();
            items.add(stack);
        }

        return items;
    }

    public ChestItemsLoadedEvent(GenericContainerScreen containerScreen) {
        this.containerScreen = containerScreen;
        this.items = initItems();
        this.fire();
    }

    public boolean isLootChest() {
        return containerScreen.getTitle().getString().startsWith("Loot Chest ");
    }

    public boolean isFlyingChest() {
        return containerScreen.getTitle().getString().contains("Flying Chest");
    }

    public boolean isChallengeReward() {
        return containerScreen.getTitle().getString().equals("Challenge Rewards");
    }


    private static boolean itemsLoaded = false;
    private static int openTicks;

    public static void onScreenRender(ScreenRenderEvent event) {
        if (!(event.screen instanceof GenericContainerScreen container)) return;
        if (itemsLoaded) {
            openTicks++;
            if (openTicks == 3) new ChestItemsLoadedEvent(container);
            return;
        }

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
        }
    }

    public static void onScreenOpened() {
        openTicks = 0;
        itemsLoaded = false;
    }

}
