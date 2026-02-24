package sidly.wynnadhoc.config.catagories;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDraggableList;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import sidly.wynnadhoc.utils.DebugWindow;

import java.util.List;

public class DebugConfig {
    @Expose
    @ConfigOption(name = "Show Debug Window", desc = "launches an entirely separate window for debug logging")
    @ConfigEditorDraggableList
    public List<DebugWindow.Priority> shownDebugging = Lists.newArrayList(DebugWindow.Priority.ERROR);

    @Expose
    @ConfigOption(name = "Disable Console Messages", desc = "Disable specific logging types cuz there annoying")
    @ConfigEditorDraggableList
    public List<ChatLoggingTypes> disabledLogging = Lists.newArrayList(ChatLoggingTypes.values());

    public enum ChatLoggingTypes {
        CHAT_MESSAGES,
        CHAT_TYPE_MESSAGES,
        DEV_PONG_EVENT,
        PAGE_NAVIGATION,
    }
}
