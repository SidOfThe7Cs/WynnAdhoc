package sidly.wynnadhoc.event;

import com.wynntils.core.text.StyledText;
import com.wynntils.utils.mc.StyledTextUtils;
import net.minecraft.text.Text;
import sidly.wynnadhoc.utils.FormatUtils;
import sidly.wynnadhoc.utils.text.ChatParser;

public class ChatMessageEvent extends Event<ChatMessageEvent> {
    public String message;
    public String asciiOnlyMessage;
    public String cleanMessage;
    public String[] splitMessage;
    public String strippedByWynntills;
    public boolean canceled = false;
    public StyledText styledText;
    public Text text;
    public ChatParser parser;

    public ChatMessageEvent(Text message, StyledText styledText) {
        this.styledText = styledText;
        this.text = message;
        this.parser = new ChatParser(message);
        this.message = message.getString();
        this.asciiOnlyMessage = FormatUtils.removeNonAscii(this.message);
        this.cleanMessage = FormatUtils.removeColorCodes(this.message);
        this.splitMessage = FormatUtils.splitByAnySpecialChar(cleanMessage);
        this.strippedByWynntills = FormatUtils.removeNonAscii(
                StyledTextUtils.unwrap(StyledText.fromString(this.message)).stripAlignment().getString()
        );
        this.fire();
    }
}
