package sidly.wynnadhoc.features.c2c.command;

import com.google.common.collect.Streams;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.netty.channel.ChannelPipeline;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.handler.DecoderHandler;
import net.minecraft.network.handler.EncoderHandler;
import net.minecraft.network.handler.PacketCodecDispatcher;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.state.NetworkState;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;
import sidly.wynnadhoc.features.c2c.C2CPacketHandler;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PacketTypeArgument implements ArgumentType<Identifier> {

    private static final Collection<String> EXAMPLES = Arrays.asList("add_entity", "minecraft:add_entity", "commands");

    private static final DynamicCommandExceptionType UNKNOWN_PACKET_EXCEPTION = new DynamicCommandExceptionType(packet -> Text.translatable("commands.clisten.unknownPacket", packet));

    public static PacketTypeArgument packet() {
        return new PacketTypeArgument();
    }

    public static Identifier getPacket(final CommandContext<FabricClientCommandSource> context, final String name) {
        return context.getArgument(name, Identifier.class);
    }

    @Override
    public Identifier parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        Identifier packetId = Identifier.fromCommandInput(reader);
        PacketTypes types = PacketTypes.get();
        if (types == null || (!types.clientbound.contains(packetId) && !types.serverbound.contains(packetId) && !types.c2cbound.contains(packetId))) {
            reader.setCursor(start);
            throw UNKNOWN_PACKET_EXCEPTION.createWithContext(reader, packetId);
        }
        return packetId;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        PacketTypes types = PacketTypes.get();
        if (types == null) {
            return builder.buildFuture();
        }
        return CommandSource.suggestIdentifiers(Streams.concat(types.clientbound.stream(), types.serverbound.stream(), types.c2cbound.stream()), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private record PacketTypes(Set<Identifier> clientbound, Set<Identifier> serverbound, Set<Identifier> c2cbound) {
        private static final Map<Object, Set<Identifier>> packetTypesCache = new WeakHashMap<>();

        private static Set<Identifier> getPacketTypes(NetworkState<?> protocolInfo) {
            return ((PacketCodecDispatcher<?, ?, PacketType<?>>) protocolInfo.codec()).typeToIndex.keySet().stream()
                    .map(PacketType::id)
                    .collect(Collectors.toSet());
        }

        @Nullable
        private static PacketTypes get() {
            ClientPlayNetworkHandler connection = MinecraftClient.getInstance().getNetworkHandler();
            if (connection == null) {
                return null;
            }

            ChannelPipeline pipeline = connection.getConnection().channel.pipeline();
            var decoder = (DecoderHandler<?>) pipeline.get("decoder");
            var clientbound = packetTypesCache.computeIfAbsent(decoder, k -> getPacketTypes(decoder.state));
            var encoder = (EncoderHandler<?>) pipeline.get("encoder");
            var serverbound = packetTypesCache.computeIfAbsent(encoder, k -> getPacketTypes(encoder.state));
            var c2cbound = packetTypesCache.computeIfAbsent("c2c", k -> getPacketTypes(C2CPacketHandler.C2C));
            return new PacketTypes(clientbound, serverbound, c2cbound);
        }
    }
}
