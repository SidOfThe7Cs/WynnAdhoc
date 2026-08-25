package sidly.wynnadhoc.mixin.client;

import com.wynntils.core.text.StyledText;
import com.wynntils.mc.event.SystemMessageEvent;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.ClickEvent.*;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.event.ChatMessageEvent;

import java.util.Arrays;
import java.util.List;

@Mixin(value = com.wynntils.handlers.chat.ChatHandler.class, remap = false)
public class ChatMixin {
    @Inject(method = "onSystemChatReceived", at = @At("HEAD" ))
    private void onSystemChatReceived(SystemMessageEvent.ChatReceivedEvent event, CallbackInfo ci) {
        String plain = event.getMessage().getString();
        ChatMessageEvent chatMessageEvent = new ChatMessageEvent(plain, event.getStyledText());
        event.setMessage(chatMessageEvent.styledText.getComponent());
        if (chatMessageEvent.canceled) {
            event.setCanceled(true);
        }

        MutableText finalText = Text.literal("" );

        StyledText[] textParts = event.getStyledText().getPartsAsTextArray();
        Arrays.stream(textParts).forEach(part -> {
            List<Text> text = part.getComponent().getSiblings();
            text.forEach(t -> {
                ClickEvent clickEvent = t.getStyle().getClickEvent();
                if (clickEvent != null) {
                    String clickEventString = clickEventToString(clickEvent);
                    HoverEvent hoverEvent = new HoverEvent.ShowText(Text.literal(clickEventString));
                    finalText.append(t.copy().setStyle(t.getStyle().withHoverEvent(hoverEvent)));
                } else finalText.append(t);
            });

        });
        event.setMessage(finalText);
    }

    @Unique
    String clickEventToString(ClickEvent clickEvent) {
        if (clickEvent != null) {
            Action action = clickEvent.getAction();
            String whatDO = String.valueOf(switch (action) {
                case RUN_COMMAND -> {
                    RunCommand runCmd = (RunCommand) clickEvent;
                    yield runCmd.command();
                }

                case SUGGEST_COMMAND -> {
                    SuggestCommand suggestCmd = (SuggestCommand) clickEvent;
                    yield suggestCmd.command();
                }

                case OPEN_URL -> {
                    OpenUrl openUrl = (OpenUrl) clickEvent;
                    yield openUrl.uri();
                }

                case COPY_TO_CLIPBOARD -> {
                    CopyToClipboard copyCmd = (CopyToClipboard) clickEvent;
                    yield copyCmd.value();
                }

                case OPEN_FILE -> {
                    OpenFile openFile = (OpenFile) clickEvent;
                    yield openFile.file();
                }

                case SHOW_DIALOG -> {
                    // For SHOW_DIALOG, this shows a dialog (like trade UI)
                    ShowDialog showDialog = (ShowDialog) clickEvent;
                    // This is more complex - might need special handling
                    yield "[SHOW_DIALOG: " + showDialog + "]";
                }

                case CHANGE_PAGE -> {
                    // used in books
                    ChangePage changePage = (ChangePage) clickEvent;
                    yield String.valueOf(changePage.page());
                }

                case CUSTOM -> {
                    // For CUSTOM, this is plugin/mod specific
                    Custom custom = (Custom) clickEvent;
                    yield custom.id().getPath() + " " + custom.payload();
                }
            });
            if (action.equals(Action.CUSTOM)) {
                return whatDO;
            }
            return action.asString() + " " + whatDO;
        }
        return "";
    }
}
