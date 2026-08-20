package sidly.wynnadhoc.features;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import sidly.wynnadhoc.event.ChatMessageEvent;
import sidly.wynnadhoc.event.CommandRegistrationEvent;
import sidly.wynnadhoc.utils.ChatMessageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReParty {
    private static boolean waitingForPartyList = false;
    private static final List<String> savedPartyMembers = new ArrayList<>();
    private static final Pattern PARTY_LIST = Pattern.compile("^Party members: (.+)$");

    public static void registerCommands(CommandRegistrationEvent event) {
        event.register(ClientCommandManager.literal("reParty")
                .then(ClientCommandManager.literal("save")
                        .executes(ctx -> {
                            waitingForPartyList = true;
                            ChatMessageUtils.sendChatCommand("party list");
                            return 1;
                        })
                )
                .then(ClientCommandManager.literal("invite")
                        .executes(ctx -> {
                            if (savedPartyMembers.isEmpty()) {
                                ChatMessageUtils.sendChatMessage("No saved party cannot invite");
                                return 1;
                            }
                            ChatMessageUtils.sendChatCommand("party create");

                            String playerName = MinecraftClient.getInstance().player.getName().getString();
                            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

                            long delay = 0;
                            for (String member : savedPartyMembers) {
                                if (Objects.equals(member, playerName)) continue; // skip self
                                delay += 300L; // add delay before to wait for party create cmd
                                scheduler.schedule(() -> MinecraftClient.getInstance().execute(() ->
                                        ChatMessageUtils.sendChatCommand("party invite " + member)), delay, TimeUnit.MILLISECONDS);
                            }
                            // all cmds are scheduled immediately with increasing delay and shutdown waits for all currently scheduled to finish
                            scheduler.shutdown();
                            return 1;
                        })
                )
        );
    }

    public static List<String> getPartyMembers(String msg) {
        Matcher matcher = PARTY_LIST.matcher(msg);
        if (!matcher.matches()) {
            return new ArrayList<>();
        }

        String members = matcher.group(1);
        members = members.replace(" and ", " ");
        String[] parts = members.split(", ");
        List<String> result = new ArrayList<>();

        for (String part : parts) {
            result.add(part.trim());
        }

        return result;
    }

    public static void onChat(ChatMessageEvent event) {
        if (!waitingForPartyList) return;
        String msg = event.strippedByWynntills;

        if (msg.equals("You must be in a party to use this.")) {
            waitingForPartyList = false;
            return;
        }

        List<String> partyMembers = getPartyMembers(msg);
        if (!partyMembers.isEmpty()) {
            waitingForPartyList = false;
            event.canceled = true;
            savedPartyMembers.clear();
            savedPartyMembers.addAll(partyMembers);

            StringBuilder sb = new StringBuilder("Saved " + savedPartyMembers.size() + " party members as:\n");
            for (String member : savedPartyMembers) {
                sb.append(member).append("\n");
            }
            ChatMessageUtils.sendChatMessage(sb.toString());
        }
    }
}
