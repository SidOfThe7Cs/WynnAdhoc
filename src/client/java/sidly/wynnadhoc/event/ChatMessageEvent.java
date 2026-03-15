package sidly.wynnadhoc.event;

import com.wynntils.core.text.StyledText;
import com.wynntils.utils.mc.StyledTextUtils;
import sidly.wynnadhoc.utils.FormatUtils;

public class ChatMessageEvent extends Event<ChatMessageEvent> {
    public String message;
    public String asciiOnlyMessage;
    public String cleanMessage;
    public String[] splitMessage;
    public String strippedByWynntills;

    public ChatMessageEvent(String message) {
        this.message = message;
        this.asciiOnlyMessage = FormatUtils.removeNonAscii(message);
        this.cleanMessage = FormatUtils.removeColorCodes(message);
        this.splitMessage = FormatUtils.splitByAnySpecialChar(cleanMessage);
        this.strippedByWynntills = FormatUtils.removeNonAscii(
                StyledTextUtils.unwrap(StyledText.fromString(message)).stripAlignment().getString()
        );
        this.fire();
    }
}
