package sidly.wynnadhoc.features.c2c;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.session.ProfileKeysImpl;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkPhase;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.encryption.PlayerKeyPair;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.encryption.PublicPlayerSession;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.state.NetworkState;
import net.minecraft.network.state.NetworkStateBuilder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import sidly.wynnadhoc.features.c2c.command.ListenCommand;
import sidly.wynnadhoc.features.c2c.packets.MessageC2CPacket;
import sidly.wynnadhoc.utils.ChatMessageUtils;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class C2CPacketHandler implements C2CPacketListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final DynamicCommandExceptionType MESSAGE_TOO_LONG_EXCEPTION = new DynamicCommandExceptionType(d -> Text.translatable("c2cpacket.messageTooLong", d));
    private static final SimpleCommandExceptionType PUBLIC_KEY_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("c2cpacket.publicKeyNotFound"));
    private static final SimpleCommandExceptionType ENCRYPTION_FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("c2cpacket.encryptionFailed"));

    public static final NetworkState<C2CPacketListener> C2C = NetworkStateBuilder.<C2CPacketListener, C2CFriendlyByteBuf>s2c(NetworkPhase.PLAY, builder -> builder
            .add(MessageC2CPacket.ID, MessageC2CPacket.CODEC)
    ).bind(b -> (C2CFriendlyByteBuf) b);

    public static final String C2C_PACKET_HEADER = "CCΕNC:";

    private static final C2CPacketHandler instance = new C2CPacketHandler();

    private C2CPacketHandler() {
    }

    public static C2CPacketHandler getInstance() {
        return instance;
    }

    public void sendPacket(Packet<C2CPacketListener> packet, PlayerListEntry recipient) throws CommandSyntaxException {
        PublicPlayerSession session = recipient.getSession();
        if (session == null) {
            throw PUBLIC_KEY_NOT_FOUND_EXCEPTION.create();
        }
        PlayerPublicKey ppk = session.publicKeyData();
        //noinspection ConstantValue
        if (ppk == null) {
            throw PUBLIC_KEY_NOT_FOUND_EXCEPTION.create();
        }
        PublicKey key = ppk.data().key();
        PacketByteBuf buf = wrapByteBuf(FriendlyByteBufs.create(), null, null);
        if (buf == null) {
            return;
        }
        C2C.codec().encode(buf, packet);
        byte[] uncompressed = new byte[buf.readableBytes()];
        buf.getBytes(0, uncompressed);
        byte[] compressed = ConversionHelper.Gzip.compress(uncompressed);
        if (compressed == null) {
            return;
        }
        // split compressed into 245 byte chunks
        int chunks = (compressed.length + 244) / 245;
        byte[][] chunked = new byte[chunks][];
        for (int i = 0; i < chunks; i++) {
            int start = i * 245;
            int end = Math.min(start + 245, compressed.length);
            chunked[i] = new byte[end - start];
            System.arraycopy(compressed, start, chunked[i], 0, end - start);
        }
        // encrypt each chunk
        byte[][] encrypted = new byte[chunks][];
        for (int i = 0; i < chunks; i++) {
            encrypted[i] = ConversionHelper.RsaEcb.encrypt(chunked[i], key);
            if (encrypted[i] == null || encrypted[i].length == 0) {
                throw ENCRYPTION_FAILED_EXCEPTION.create();
            }
        }
        // join encrypted chunks into one byte array
        byte[] joined = new byte[encrypted.length * 256];
        for (int i = 0; i < encrypted.length; i++) {
            System.arraycopy(encrypted[i], 0, joined, i * 256, 256);
        }
        String packetString = ConversionHelper.BaseUTF8.toUnicode(joined);
        String commandString = "w " + recipient.getProfile().name() + ' ' + C2C_PACKET_HEADER + packetString;
        if (commandString.length() >= 256) {
            throw MESSAGE_TOO_LONG_EXCEPTION.create(commandString.length());
        }
        ListenCommand.onPacket(packet, ListenCommand.PacketFlow.C2C_OUTBOUND);
        ChatMessageUtils.sendChatCommand(commandString);
        OutgoingPacketFilter.addPacket(packetString);
    }

    public static boolean handleC2CPacket(String content, String sender, UUID senderUUID) {
        byte[] encrypted = ConversionHelper.BaseUTF8.fromUnicode(content);
        // round down to multiple of 256 bytes
        int length = encrypted.length & ~0xFF;
        // copy to new array of arrays
        byte[][] encryptedArrays = new byte[length / 256][];
        for (int i = 0; i < length; i += 256) {
            encryptedArrays[i / 256] = Arrays.copyOfRange(encrypted, i, i + 256);
        }
        if (!(MinecraftClient.getInstance().getProfileKeys() instanceof ProfileKeysImpl profileKeyPairManager)) {
            return false;
        }
        Optional<PlayerKeyPair> keyPair = profileKeyPairManager.fetchKeyPair().join();
        if (keyPair.isEmpty()) {
            return false;
        }
        // decrypt
        int len = 0;
        byte[][] decryptedArrays = new byte[encryptedArrays.length][];
        for (int i = 0; i < encryptedArrays.length; i++) {
            decryptedArrays[i] = ConversionHelper.RsaEcb.decrypt(encryptedArrays[i], keyPair.get().privateKey());
            if (decryptedArrays[i] == null) {
                return false;
            }
            len += decryptedArrays[i].length;
        }
        // copy to new array
        byte[] decrypted = new byte[len];
        int pos = 0;
        for (byte[] decryptedArray : decryptedArrays) {
            System.arraycopy(decryptedArray, 0, decrypted, pos, decryptedArray.length);
            pos += decryptedArray.length;
        }
        byte[] uncompressed = ConversionHelper.Gzip.decompress(decrypted);
        if (uncompressed == null) {
            return false;
        }
        C2CFriendlyByteBuf buf = wrapByteBuf(Unpooled.wrappedBuffer(uncompressed), sender, senderUUID);
        if (buf == null) {
            return false;
        }
        C2CPacket packet;
        try {
            packet = (C2CPacket) C2C.codec().decode(buf);
        } catch (Throwable e) {
            LOGGER.error("Error decoding C2C packet", e);
            return false;
        }
        if (buf.readableBytes() > 0) {
            LOGGER.error("Found extra bytes while reading C2C packet {}", packet.getPacketType());
            return false;
        }
        if (!sender.equals(packet.sender())) {
            LOGGER.error("Detected mismatching packet sender. Expected {}, got {}", sender, packet.sender());
            return false;
        }
        ListenCommand.onPacket(packet, ListenCommand.PacketFlow.C2C_INBOUND);
        try {
            packet.apply(C2CPacketHandler.getInstance());
        } catch (Throwable e) {
            ChatMessageUtils.sendChatMessage(Text.of(e.getMessage()));
            LOGGER.error("Error handling C2C packet", e);
        }
        return true;
    }

    @Override
    public void onMessageC2CPacket(MessageC2CPacket packet) {
        String sender = packet.sender();
        String message = packet.message();
        Text formattedComponent = Text.of(message);
        /*
        try {
            formattedComponent = ExtendedMarkdownArgument.extendedMarkdown().parse(new StringReader(message));
        } catch (CommandSyntaxException e) {
            formattedComponent = Text.of(message);
        }

         */
        MutableText prefix = Text.empty();
        prefix.append(Text.literal("[").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)));
        prefix.append(Text.literal("/cwe").setStyle(Style.EMPTY.withColor(Formatting.AQUA)));
        prefix.append(Text.literal("]").setStyle(Style.EMPTY.withColor(Formatting.DARK_GRAY)));
        prefix.append(Text.literal(" "));
        Text text = prefix.append(Text.translatable("c2cpacket.messageC2CPacket.incoming", sender, formattedComponent));
        ChatMessageUtils.sendChatMessage(text);
    }

    public static @Nullable C2CFriendlyByteBuf wrapByteBuf(ByteBuf buf, @Nullable String sender, @Nullable UUID senderUUID) {
        ClientPlayNetworkHandler connection = MinecraftClient.getInstance().getNetworkHandler();
        if (connection == null) {
            return null;
        }
        return new C2CFriendlyByteBuf(buf, connection.getRegistryManager(), sender, senderUUID);
    }

    @Override
    public NetworkPhase getPhase() {
        return NetworkPhase.PLAY;
    }

    @Override
    public void onDisconnected(DisconnectionInfo info) {

    }

    @Override
    public boolean isConnectionOpen() {
        return true;
    }
}
