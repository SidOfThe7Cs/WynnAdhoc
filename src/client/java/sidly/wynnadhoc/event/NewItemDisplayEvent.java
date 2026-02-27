package sidly.wynnadhoc.event;

import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.Item;
import sidly.wynnadhoc.utils.ItemUtils;

public class NewItemDisplayEvent extends Event<NewItemDisplayEvent> {
    public DisplayEntity.ItemDisplayEntity itemDisplay;
    public Item item;
    public Float customModel;

    public NewItemDisplayEvent(DisplayEntity.ItemDisplayEntity itemDisplay) {
        this.itemDisplay = itemDisplay;
        this.item = itemDisplay.getItemStack().getItem();
        this.customModel = ItemUtils.getFirsCustomModelDataFloat(itemDisplay.getItemStack());
        this.fire();
    }

    public static void onEachEntity(ForEachEntityEvent event) {
        if (event.entity instanceof DisplayEntity.ItemDisplayEntity display) {
            if (event.isNew) new NewItemDisplayEvent(display);
        }
    }
}
