package sidly.wynnadhoc.mixin.client;

import com.wynntils.core.text.StyledText;
import com.wynntils.features.chat.RevealNicknamesFeature;
import com.wynntils.handlers.chat.event.ChatMessageEvent;
import com.wynntils.utils.mc.StyledTextUtils;
import com.wynntils.utils.type.IterationDecision;
import net.minecraft.text.HoverEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.utils.PlayerUtils;

import java.util.regex.Matcher;


@Mixin(value = RevealNicknamesFeature.class, remap = false)
public class NicknameMixin {

    @Inject(method = "onPlayerChat",
            at = @At("HEAD"),
            remap = false)
    private void onPlayerChat(ChatMessageEvent.Edit event, CallbackInfo ci) {
        event.getMessage().iterate((currentPart, changes) -> {
            HoverEvent hoverEvent = currentPart.getPartStyle().getStyle().getHoverEvent();
            if (hoverEvent != null && hoverEvent.getAction() == HoverEvent.Action.SHOW_TEXT) {
                HoverEvent.ShowText showTextHoverEvent = (HoverEvent.ShowText) hoverEvent;
                StyledText[] partTexts = StyledText.fromComponent(showTextHoverEvent.value()).split("\n");
                String nickname = null;
                String username = null;

                for (StyledText partText : partTexts) {
                    Matcher nicknameMatcher = partText.getMatcher(StyledTextUtils.NICKNAME_PATTERN);
                    if (nicknameMatcher.matches()) {
                        nickname = nicknameMatcher.group("nick");
                        username = nicknameMatcher.group("username");
                    }
                }

                if (nickname != null && username != null) {
                    PlayerUtils.addNick(nickname, username);
                }
            }
            return IterationDecision.CONTINUE;
        });
    }
}
