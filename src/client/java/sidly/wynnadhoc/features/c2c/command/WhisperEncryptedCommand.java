package sidly.wynnadhoc.features.c2c.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sidly.wynnadhoc.event.CommandRegistrationEvent;
import sidly.wynnadhoc.features.c2c.C2CConnectionManager;
import sidly.wynnadhoc.features.c2c.packets.MessageC2CPacket;
import sidly.wynnadhoc.utils.ChatMessageUtils;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class WhisperEncryptedCommand {
    public static void register(CommandRegistrationEvent event) {
        event.dispatcher.register(literal("cwe")
                .then(argument("player", StringArgumentType.word())
                        .then(argument("message", StringArgumentType.string())
                                .executes((ctx) ->
                                        whisper(
                                                ctx.getSource(),
                                                StringArgumentType.getString(ctx, "player"),
                                                StringArgumentType.getString(ctx, "message")
                                        )
                                )
                        )
                )
        );
    }

    private static int whisper(FabricClientCommandSource source, String playerName, String message) {
        MessageC2CPacket packet = new MessageC2CPacket(source.getClient().getNetworkHandler().getProfile().name(), message);
        int result = C2CConnectionManager.queuePacket(packet, playerName);
        if (result == 1) {
            MutableText prefix = Text.empty();
            prefix.append(Text.literal("[").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)));
            prefix.append(Text.literal("/cwe").setStyle(Style.EMPTY.withColor(Formatting.AQUA)));
            prefix.append(Text.literal("]").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)));
            prefix.append(Text.literal(" "));
            Text component = prefix.append(Text.literal("to " + playerName + ": " + message));
            source.sendFeedback(component);
            return Command.SINGLE_SUCCESS;
        } else {
            ChatMessageUtils.sendChatMessage(playerName + " does not have wynnadhoc installed");
            return 0;
        }
    }
}
