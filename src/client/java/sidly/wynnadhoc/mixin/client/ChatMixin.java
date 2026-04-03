package sidly.wynnadhoc.mixin.client;

import com.wynntils.mc.event.SystemMessageEvent;
import net.minecraft.text.*;
import net.minecraft.text.ClickEvent.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.event.ChatMessageEvent;
import sidly.wynnadhoc.mixin.client.accessors.StyledTextAccessor;

import java.util.List;

@Mixin(value = com.wynntils.handlers.chat.ChatHandler.class, remap = false)
public class ChatMixin {
    @Inject(method = "onSystemChatReceived", at = @At("HEAD"))
    private void onSystemChatReceived(SystemMessageEvent.ChatReceivedEvent event, CallbackInfo ci) {
        String plain = event.getMessage().getString();
        new ChatMessageEvent(plain);

        StyledTextAccessor accessor = ((StyledTextAccessor) (Object) event.getStyledText());
        if (accessor == null) return;
        List<ClickEvent> clickEvents = accessor.getClickEvents();
        HoverEvent hoverEvent = null;
        boolean noHoverEvents = accessor.getHoverEvents().isEmpty();
        for (ClickEvent clickEvent : clickEvents) {
            String clickEventString = clickEventToString(clickEvent);
            if (ConfigManager.INSTANCE.config.debug.showCmdOnChatHover && noHoverEvents) {
                hoverEvent = new HoverEvent.ShowText(Text.literal(clickEventString));
            }
        }

        // copy to avoid infinite recursion
        if (noHoverEvents && hoverEvent != null) {
            Style style = event.getMessage().getStyle();
            Style newStyle = style.withHoverEvent(hoverEvent);
            ((MutableText) event.getMessage()).setStyle(newStyle);
        }
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
                    yield custom.id() + " " + custom.payload();
                }
            });
            return action.asString() + " " + whatDO;
        }
        return "";
    }
}
