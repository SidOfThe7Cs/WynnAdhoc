package sidly.wynnadhoc.mixin.client;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.event.SlotClickedEvent;

@Mixin(ScreenHandler.class)
public class SlotClickedMixin {
    @Inject(method = "onSlotClick", at = @At("HEAD"))
    private void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        ScreenHandler handler = player.currentScreenHandler;
        new SlotClickedEvent(handler, slotIndex, button, actionType, player);
    }
}
