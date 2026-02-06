package sidly.wynnadhoc.mixin.client.accessors;

import com.wynntils.models.items.items.gui.TerritoryItem;
import com.wynntils.utils.type.Pair;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(com.wynntils.screens.territorymanagement.TerritoryManagementScreen.class)
public interface TerritoryManagementScreenAccessor {
    @Accessor(remap = false)
    List<Pair<ItemStack, TerritoryItem>> getTerritoryItems();
}
