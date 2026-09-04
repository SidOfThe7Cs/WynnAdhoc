package sidly.wynnadhoc.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.features.c2c.C2CPacketHandler;
import sidly.wynnadhoc.features.c2c.OutgoingPacketFilter;
import sidly.wynnadhoc.utils.ChatMessageUtils;

import java.time.Instant;

@Mixin(MessageHandler.class)
public class ChatListenerMixin {
    @Inject(method = "processChatMessageInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageTrustStatus;createIndicator(Lnet/minecraft/network/message/SignedMessage;)Lnet/minecraft/client/gui/hud/MessageIndicator;"), cancellable = true)
    private void onC2CPacket(MessageType.Parameters params, SignedMessage message, Text decorated, GameProfile sender, boolean onlyShowSecureChat, Instant receptionTimestamp, CallbackInfoReturnable<Boolean> cir) {
        String string = message.getSignedContent();
        int index = string.indexOf(C2CPacketHandler.C2C_PACKET_HEADER);
        if (index == -1) {
            return;
        }
        String packetString = string.substring(index + C2CPacketHandler.C2C_PACKET_HEADER.length());
        if (ConfigManager.INSTANCE.config.c2c.allowC2C) {
            if (OutgoingPacketFilter.removeIfContains(packetString)) {
                ChatMessageUtils.sendChatMessage("c2cpacket.sentC2CPacket");
            } else {
                ChatMessageUtils.sendChatMessage(Text.translatable("c2cpacket.receivedC2CPacket").setStyle(
                        Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(params.applyChatDecoration(message.getContent())))
                ));
            }
            cir.setReturnValue(false);
            return;
        }
        if (OutgoingPacketFilter.removeIfContains(packetString)) {
            cir.setReturnValue(false);
            return;
        }
        if (C2CPacketHandler.handleC2CPacket(packetString, sender.name(), sender.id())) {
            cir.setReturnValue(true);
        } else {
            ChatMessageUtils.sendChatMessage(Text.translatable("c2cpacket.malformedPacket").setStyle(Style.EMPTY.withColor(Formatting.RED)));
        }
    }
}
