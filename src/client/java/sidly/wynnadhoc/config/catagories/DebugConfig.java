package sidly.wynnadhoc.config.catagories;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean;
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDraggableList;
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption;
import sidly.wynnadhoc.utils.Debug;

import java.util.List;

public class DebugConfig {
    @Expose
    @ConfigOption(name = "Show Cmd On Chat Hover", desc = "shows what a clickable chat message will do if you click it when you hover it")
    @ConfigEditorBoolean
    public boolean showCmdOnChatHover = false;

    @Expose
    @ConfigOption(name = "Shown Debug Info", desc = "what types of debug info should be shown")
    @ConfigEditorDraggableList
    public List<Debug.Type> shownDebugging = Lists.newArrayList(Debug.Type.MANUAL, Debug.Type.TEMP);

    @Expose
    @ConfigOption(name = "Show Debug Window", desc = "@Deprecated Uses a new window for logging")
    @ConfigEditorBoolean
    public boolean newWindow = false;

    @Expose
    @ConfigOption(name = "Disable Console Messages", desc = "Disable specific logging types cuz there annoying")
    @ConfigEditorDraggableList
    public List<ChatLoggingTypes> disabledLogging = Lists.newArrayList(ChatLoggingTypes.values());

    public enum ChatLoggingTypes {
        CHAT_MESSAGES,
        CHAT_TYPE_MESSAGES,
        DEV_PONG_EVENT,
        PAGE_NAVIGATION,
        ACTION_BAR_FAILURE,
    }
}
