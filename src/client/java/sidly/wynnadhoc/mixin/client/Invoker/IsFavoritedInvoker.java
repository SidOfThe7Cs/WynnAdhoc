package sidly.wynnadhoc.mixin.client.Invoker;

import com.wynntils.features.inventory.ItemFavoriteFeature;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemFavoriteFeature.class)
public interface IsFavoritedInvoker {
    @Invoker("isFavorited") boolean invokeIsFavorited(ItemStack itemStack);
}
