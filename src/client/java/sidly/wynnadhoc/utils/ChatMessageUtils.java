package sidly.wynnadhoc.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatMessageUtils {

    public static void sendChatCommand(String command) {
        if (command == null || command.isEmpty()) return;
        String cmd = command.startsWith("/") ? command.substring(1) : command;
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            if (player != null) {
                player.networkHandler.sendChatCommand(cmd);
            }
        });
    }

    public static void sendChatMessage(String message) {
        sendChatMessage(Text.literal(message));
    }

    public static void sendChatMessage(Text text) {
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            if (player != null && text != null) {
                player.sendMessage(text, false);
            }
        });
    }

    public static void sendChatMessage(List<Text> text) {
        MinecraftClient.getInstance().execute(() -> {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            if (player != null && text != null) {
                for (Text t : text) {
                    player.sendMessage(t, false);
                }
            }
        });
    }

    public static String[] extractTextBeforeSpecificString(String input, String specificString) {
        // Define regex pattern to match everything before the specific string
        Pattern pattern = Pattern.compile("(.*?)" + Pattern.quote(specificString));
        Matcher matcher = pattern.matcher(input);

        String beforeSpecific = "";
        String afterSpecific = "";
        String extracted = "";
        String remaining = "";

        if (matcher.find()) {
            // Extract text up to the specific string
            String textUpToSpecific = matcher.group(1).trim();

            // Find the last space before the specific string
            int lastSpaceIndex = textUpToSpecific.lastIndexOf(' ');
            if (lastSpaceIndex != -1) {
                extracted = textUpToSpecific.substring(lastSpaceIndex + 1).trim();
                beforeSpecific = input.substring(0, lastSpaceIndex - 1).trim();
            } else {
                extracted = textUpToSpecific; // No space found, take the whole text
                beforeSpecific = "";
            }

            // Extract text after the specific string
            afterSpecific = input.substring(matcher.end()).trim();
            remaining = beforeSpecific + afterSpecific;
        } else return new String[]{"", input};

        return new String[]{extracted, remaining};
    }


}
