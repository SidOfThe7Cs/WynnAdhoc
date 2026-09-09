package sidly.wynnadhoc.features.c2c;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.network.packet.Packet;
import sidly.wynnadhoc.event.ClientTickEvent;
import sidly.wynnadhoc.event.CommandRegistrationEvent;
import sidly.wynnadhoc.features.c2c.packets.ConnectionRequestC2CPacket;
import sidly.wynnadhoc.utils.ChatMessageUtils;
import sidly.wynnadhoc.utils.EncryptionUtils;
import sidly.wynnadhoc.utils.PlayerUtils;

import java.util.*;

public class C2CConnectionManager {
    private static final Map<String, List<PacketData>> queue = new HashMap<>();
    private static final Set<String> failures = new HashSet<>();
    private static final Map<String, Long> lastSentMap = new HashMap<>(); // this is ticks not ms
    private static final Map<String, Long> sentConnectionRequests = new HashMap<>();
    private static final int CD = 7;
    private static final int MAX_QUEUE_SIZE = 15;
    private static final int MAX_CONN_SECONDS = 10;

    public static int queuePacket(Packet<C2CPacketListener> packet, String recipient) {
        if (failures.contains(recipient.toLowerCase())) return 0;
        PacketData packetData = new PacketData(packet, recipient);
        queue.computeIfAbsent(recipient.toLowerCase(), k -> new ArrayList<>()).add(packetData);
        return 1;
    }

    public static void onTick(ClientTickEvent event) {
        long ticks = event.getCounter();
        Set<String> toRemove = new HashSet<>();
        queue.forEach((name, value) -> {
            if (EncryptionUtils.INSTANCE.getPublicKey(name) == null) {
                Long lastSent = sentConnectionRequests.get(name);
                if (lastSent == null) {
                    requestConnection(name);
                    lastSentMap.put(name, ticks);
                } else if (lastSent + MAX_CONN_SECONDS * 1000 < System.currentTimeMillis()) {
                    onFailure(name);
                    toRemove.add(name);
                }
                return;
            }
            if (value.size() > MAX_QUEUE_SIZE) {
                onFailure(name);
                toRemove.add(name);
                return;
            }
            Long lastSent = lastSentMap.get(name);
            if (lastSent == null || lastSent + CD < ticks) {
                if (value.isEmpty()) return;
                PacketData packetData = value.removeFirst();
                packetData.send();
                lastSentMap.put(name, ticks);
            }
        });
        toRemove.forEach(queue::remove);
    }

    public static void onFailure(String recipient) {
        failures.add(recipient);
        sentConnectionRequests.remove(recipient);
        ChatMessageUtils.sendChatMessage(recipient + " Does not appear to have wynnadhoc installed further C2C packets will not be sent, retry with /wynnadhoc c2c connnect " + recipient);
    }

    public static void onConnect(String recipient) {
        failures.remove(recipient.toLowerCase());
        sentConnectionRequests.remove(recipient.toLowerCase());
    }

    public static int requestConnection(String recipient) {
        failures.remove(recipient);
        sentConnectionRequests.put(recipient, System.currentTimeMillis());
        EncryptionUtils.INSTANCE.removeKey(recipient);
        String pub = EncryptionUtils.INSTANCE.publicKeyToBase64(EncryptionUtils.INSTANCE.getPublicKey());
        ConnectionRequestC2CPacket packet = new ConnectionRequestC2CPacket(PlayerUtils.self(), pub);
        try {
            C2CPacketHandler.getInstance().sendPacket(packet, recipient, false);
            return 1;
        } catch (CommandSyntaxException e) {
            ChatMessageUtils.sendChatMessage(e.getMessage());
            return 0;
        }
    }

    public static void register(CommandRegistrationEvent event) {
        event.register(ClientCommandManager.literal("c2c")
                .then(ClientCommandManager.literal("connnect") // it doesnt work if i spell it right ¯\_(ツ)_/¯
                        .then(ClientCommandManager.argument("user", StringArgumentType.string())
                                .executes((ctx) ->
                                        requestConnection(
                                                StringArgumentType.getString(ctx, "user").toLowerCase()
                                        )
                                )
                        )
                )
        );
    }
}
