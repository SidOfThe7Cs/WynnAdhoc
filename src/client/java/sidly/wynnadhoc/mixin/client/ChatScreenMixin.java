package sidly.wynnadhoc.mixin.client;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sidly.wynnadhoc.utils.ClickEventFunction;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Inject(method = "handleClickEvent", at = @At("HEAD" ))
    public void handleClickEvent(Style style, boolean insert, CallbackInfoReturnable<Boolean> cir) {
        ClickEvent clickEvent = style.getClickEvent();
        if (clickEvent instanceof ClickEvent.Custom(Identifier id, java.util.Optional<NbtElement> payload1)) {
            if (id.equals(ClickEventFunction.Companion.getRUN_FUNCTION_ID())) {
                NbtElement payload = payload1.orElse(null);
                if (payload instanceof NbtString) {
                    payload.asString().ifPresent(n -> ClickEventFunction.valueOf(n).onClick());
                }
            }
        }
    }

}
