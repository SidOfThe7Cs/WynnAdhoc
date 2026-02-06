package sidly.wynnadhoc.event;

import sidly.wynnadhoc.utils.FormatUtils;

public class ChatMessageEvent extends Event<ChatMessageEvent> {
    public String message;
    public String asciiOnlyMessage;
    public String cleanMessage;
    public String[] splitMessage;

    public ChatMessageEvent(String message) {
        this.message = message;
        this.asciiOnlyMessage = FormatUtils.removeNonAscii(message);
        this.cleanMessage = FormatUtils.removeColorCodes(message);
        this.splitMessage = FormatUtils.splitByAnySpecialChar(cleanMessage);
        this.fire();
    }
}
