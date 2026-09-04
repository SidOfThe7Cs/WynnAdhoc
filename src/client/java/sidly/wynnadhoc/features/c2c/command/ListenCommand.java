package sidly.wynnadhoc.features.c2c.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.property.Property;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import sidly.wynnadhoc.event.CommandRegistrationEvent;
import sidly.wynnadhoc.features.c2c.ReflectionUtils;
import sidly.wynnadhoc.features.c2c.UnsafeUtils;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Array;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static sidly.wynnadhoc.features.c2c.command.PacketTypeArgument.getPacket;
import static sidly.wynnadhoc.features.c2c.command.PacketTypeArgument.packet;

public class ListenCommand {

    private static final SimpleCommandExceptionType ALREADY_LISTENING_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.clisten.add.failed"));
    private static final SimpleCommandExceptionType NOT_LISTENING_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.clisten.remove.failed"));

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Set<Identifier> packets = new HashSet<>();

    private static @UnknownNullability PacketCallback callback;

    public static void register(CommandRegistrationEvent event) {
        event.dispatcher.register(literal("clisten")
                .then(literal("add")
                        .then(argument("packet", packet())
                                .executes(ctx -> add(ctx.getSource(), getPacket(ctx, "packet")))))
                .then(literal("remove")
                        .then(argument("packet", packet())
                                .executes(ctx -> remove(ctx.getSource(), getPacket(ctx, "packet")))))
                .then(literal("list")
                        .executes(ctx -> list(ctx.getSource())))
                .then(literal("clear")
                        .executes(ctx -> clear(ctx.getSource()))));
    }

    private static int add(FabricClientCommandSource source, Identifier packetType) throws CommandSyntaxException {
        if (!packets.add(packetType)) {
            throw ALREADY_LISTENING_EXCEPTION.create();
        }

        source.sendFeedback(Text.translatable("commands.clisten.add.success"));

        if (callback == null) {
            callback = (packet, side) -> source.getClient().send(() -> {
                String packetData;
                Text packetDataPreview;
                try {
                    packetDataPreview = serialize(packet, new ReferenceOpenHashSet<>(), 0);
                    packetData = packetDataPreview.getString();
                } catch (StackOverflowError e) {
                    LOGGER.error("Could not serialize packet into a Text", e);
                    return;
                }

                String packetClassName = packet.getClass().getName().replace('.', '/');
                packetClassName = packetClassName.substring(packetClassName.lastIndexOf('/') + 1);

                MutableText packetComponent = Text.literal(packetClassName).setStyle(Style.EMPTY
                        .withUnderline(true)
                        .withHoverEvent(new HoverEvent.ShowText(packetDataPreview))
                        .withClickEvent(new ClickEvent.CopyToClipboard(packetData)));

                switch (side) {
                    case SERVERBOUND ->
                            source.sendFeedback(Text.translatable("commands.clisten.sentPacket", packetComponent));
                    case CLIENTBOUND ->
                            source.sendFeedback(Text.translatable("commands.clisten.receivedPacket", packetComponent));
                    case C2C_OUTBOUND ->
                            source.sendFeedback(Text.translatable("commands.clisten.sentC2CPacket", packetComponent));
                    case C2C_INBOUND ->
                            source.sendFeedback(Text.translatable("commands.clisten.receivedC2CPacket", packetComponent));
                }
            });
        }

        return Command.SINGLE_SUCCESS;
    }

    private static int remove(FabricClientCommandSource source, net.minecraft.util.Identifier packetType) throws CommandSyntaxException {
        if (!packets.remove(packetType)) {
            throw NOT_LISTENING_EXCEPTION.create();
        }

        source.sendFeedback(Text.translatable("commands.clisten.remove.success"));
        return Command.SINGLE_SUCCESS;
    }

    private static int list(FabricClientCommandSource source) throws CommandSyntaxException {
        int amount = packets.size();
        if (amount == 0) {
            source.sendFeedback(Text.translatable("commands.clisten.list.none"));
        } else {
            source.sendFeedback(Text.translatable("commands.clisten.list"));
            for (Identifier packetType : packets) {
                source.sendFeedback(Text.literal(packetType.toString()));
            }
        }

        return amount;
    }

    private static int clear(FabricClientCommandSource source) throws CommandSyntaxException {
        int amount = packets.size();
        packets.clear();
        source.sendFeedback(Text.translatable("commands.clisten.clear"));
        return amount;
    }

    private static Text serialize(@Nullable Object object, Set<@Nullable Object> seen, int depth) {
        try {
            if (depth <= 10 && seen.add(object)) {
                return serializeInner(object, seen, depth);
            }
            return Text.empty();
        } finally {
            seen.remove(object);
        }
    }

    private static Text serializeInner(@Nullable Object object, Set<@Nullable Object> seen, int depth) {
        return switch (object) {
            case null -> Text.literal("null");
            case Text text -> text;
            case String string -> Text.literal(string);
            case Number number -> Text.literal(number.toString());
            case Boolean bool -> Text.literal(bool.toString());
            case Optional<?> optional ->
                    optional.map(o -> serialize(o, seen, depth + 1)).orElseGet(() -> Text.literal("empty"));
            case Date date -> Text.of(date);
            case Instant instant -> Text.of(Date.from(instant));
            case UUID uuid -> Text.of(uuid);
            case ChunkPos chunkPos -> Text.of(chunkPos);
            case Identifier identifier -> Text.of(identifier);
            case Message message -> Text.of(message);
            case Collection<?> collection -> {
                MutableText text = Text.literal("[");
                text.append(collection.stream().map(e -> asMutable(serialize(e, seen, depth + 1))).reduce((l, r) -> l.append(Texts.DEFAULT_SEPARATOR_TEXT).append(r)).orElseGet(Text::empty));
                yield text.append("]");
            }
            case Map<?, ?> map -> {
                MutableText text = Text.literal("{");
                text.append(map.entrySet().stream().map(e -> asMutable(serialize(e.getKey(), seen, depth + 1)).append("=").append(serialize(e.getValue(), seen, depth + 1))).reduce((l, r) -> l.append(Texts.DEFAULT_SEPARATOR_TEXT).append(r)).orElseGet(Text::empty));
                yield text.append("}");
            }
            case Registry<?> registry -> Text.of(registry.getKey().getValue());
            case RegistryKey<?> resourceKey -> {
                MutableText text = Text.literal("{");
                text.append("registry=").append(serialize(resourceKey.getRegistry(), seen, depth + 1)).append(Texts.DEFAULT_SEPARATOR_TEXT);
                text.append("identifier=").append(serialize(resourceKey.getValue(), seen, depth + 1));
                yield text.append("}");
            }
            case RegistryEntry<?> holder -> {
                MutableText text = Text.literal("{");
                text.append("kind=").append(serialize(holder.getType().name(), seen, depth + 1)).append(Texts.DEFAULT_SEPARATOR_TEXT);
                text.append("value=").append(serialize(holder.value(), seen, depth + 1));
                yield text.append("}");
            }
            case BlockState state -> {
                MutableText text = asMutable(serialize(state.getBlock(), seen, depth));
                if (!state.getProperties().isEmpty()) {
                    text.append(state.getProperties().stream().map(property -> property.getName() + "=" + getProperty(state, property)).collect(Collectors.joining(", ", "[", "]")));
                }
                yield text;
            }
            case Block block -> Text.literal(Registries.BLOCK.getKey(block).toString());
            case Item item -> Text.literal(Registries.ITEM.getKey(item).toString());
            case EntityType<?> entityType -> Text.literal(Registries.ENTITY_TYPE.getKey(entityType).toString());
            default -> {
                if (object.getClass().isArray()) {
                    MutableText text = Text.literal("[");
                    int lengthMinusOne = Array.getLength(object) - 1;
                    if (lengthMinusOne < 0) {
                        yield text.append("]");
                    }
                    for (int i = 0; i < lengthMinusOne; i++) {
                        text.append(serialize(Array.get(object, i), seen, depth + 1)).append(Texts.DEFAULT_SEPARATOR_TEXT);
                    }
                    yield text.append(serialize(Array.get(object, lengthMinusOne), seen, depth + 1)).append("]");
                }

                String className = object.getClass().getName().replace(".", "/");
                className = className.substring(className.lastIndexOf('/') + 1);

                MutableText text = Text.literal(className + '{');
                text.append(ReflectionUtils.getAllFields(object.getClass())
                        .filter(field -> !Modifier.isStatic(field.getModifiers()))
                        .map(field -> {
                            String fieldName = field.getName();
                            try {
                                field.setAccessible(true);
                                return Text.literal(fieldName + '=').append(serialize(field.get(object), seen, depth + 1));
                            } catch (InaccessibleObjectException | ReflectiveOperationException e) {
                                try {
                                    MethodHandles.Lookup implLookup = UnsafeUtils.getImplLookup();
                                    if (implLookup == null) {
                                        return Text.literal(fieldName + '=').append(Text.translatable("commands.clisten.packetError").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
                                    }
                                    VarHandle varHandle = implLookup.findVarHandle(object.getClass(), fieldName, field.getType());
                                    return Text.literal(fieldName + '=').append(serialize(varHandle.get(object), seen, depth + 1));
                                } catch (ReflectiveOperationException ex) {
                                    return Text.literal(fieldName + '=').append(Text.translatable("commands.clisten.packetError").setStyle(Style.EMPTY.withColor(Formatting.DARK_RED)));
                                }
                            }
                        })
                        .reduce((l, r) -> l.append(Texts.DEFAULT_SEPARATOR_TEXT).append(r))
                        .orElseGet(Text::empty));
                yield text.append("}");
            }
        };
    }

    private static MutableText asMutable(Text text) {
        return text instanceof MutableText mutable ? mutable : text.copy();
    }

    private static <T extends Comparable<T>> String getProperty(BlockState state, Property<T> property) {
        return property.name(state.get(property));
    }

    public enum PacketFlow {
        SERVERBOUND,
        CLIENTBOUND,
        C2C_OUTBOUND,
        C2C_INBOUND
    }

    public static void onPacket(Packet<?> packet, PacketFlow side) {
        if (!packets.contains(packet.getPacketType().id())) {
            return;
        }
        callback.apply(packet, side);
    }

    @FunctionalInterface
    private interface PacketCallback {
        void apply(Packet<?> packet, PacketFlow side);
    }
}
