package sidly.wynnadhoc.mixin.client;

import com.wynntils.mc.event.SystemMessageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.event.ChatMessageEvent;

@Mixin(value = com.wynntils.handlers.chat.ChatHandler.class, remap = false)
public class ChatMixin {
    @Inject(method = "onSystemChatReceived", at = @At("HEAD"))
    private void onSystemChatReceived(SystemMessageEvent.ChatReceivedEvent event, CallbackInfo ci) {
        String plain = event.getMessage().getString();
        new ChatMessageEvent(plain);
    }
}
