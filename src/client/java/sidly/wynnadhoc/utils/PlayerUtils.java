package sidly.wynnadhoc.utils;

import sidly.wynnadhoc.config.ConfigManager;

import java.util.HashMap;
import java.util.Map;

public class PlayerUtils {
    private static final Map<String, String> nicknameMap = new HashMap<>();

    public static String getRealName(String nick) {
        return nicknameMap.get(nick);
    }

    public static void addNick(String nick, String realName) {
        if (nicknameMap.containsKey(nick)) return;
        nicknameMap.put(nick, realName);
        ConfigManager.INSTANCE.config.guild.aspectOverlayData.updateDisplay();
    }
}
