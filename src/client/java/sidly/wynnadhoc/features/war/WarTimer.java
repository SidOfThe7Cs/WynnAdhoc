package sidly.wynnadhoc.features.war;


import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.event.ChatMessageEvent;
import sidly.wynnadhoc.utils.FormatUtils;

import java.time.temporal.ChronoUnit;

public class WarTimer {
    private static long startAt = 0; // epoch millis

    public static boolean isActive() {
        return startAt >= System.currentTimeMillis();
    }

    public static void setTo25s() {
        startAt = System.currentTimeMillis() + 25000;
    }

    public static String getDisplay() {
        if (!isActive()) return "";
        long millisUntilStart = startAt - System.currentTimeMillis();
        return FormatUtils.formatTime(millisUntilStart, ChronoUnit.MILLIS);
    }

    public static void onChatMessage(ChatMessageEvent event) {
        if (event.asciiOnlyMessage.contains("The war battle will start in 25 seconds")) {
            setTo25s();
        }
    }

    public static void onClientTick() {
        if (isActive()) ConfigManager.INSTANCE.config.war.warTimer.updateDisplay();
    }
}
