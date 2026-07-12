package sidly.wynnadhoc;

import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public class PacketStuff {
    public static void serverBoundPacket(Packet<?> packet, @Nullable ChannelFutureListener listener, boolean flush) {

    }

    private static final Set<String> packetSet = Set.of(
            "set_entity_data",
            "move_entity_pos_rot",
            "set_entity_motion",
            "rotate_head",
            "system_chat",
            "level_particles",
            "entity_position_sync",
            "move_entity_rot",
            "move_entity_pos",
            "sound",
            "remove_entities",
            "set_passengers",
            "set_health",
            "pong_response",
            "remove_mob_effect",
            "update_mob_effect",
            "boss_event",
            "add_entity",
            "set_subtitle_text",
            "set_title_text",
            "set_titles_animation"
    );

    private static final Set<PacketType<? extends Packet<?>>> playPacketSet = Set.of(
            PlayPackets.CONTAINER_SET_CONTENT,
            PlayPackets.CONTAINER_SET_SLOT,
            PlayPackets.CONTAINER_SET_DATA
    );

    public static void clientBoundPacker(Packet<?> packet, boolean bundled) {
        String packetName = packet.getPacketType().id().getPath();
        if (packet instanceof BundleS2CPacket bundlePacket) {
            Iterable<Packet<? super ClientPlayPacketListener>> packets = bundlePacket.getPackets();
            packets.forEach(p -> clientBoundPacker(p, true));
            return;
        }
        if (!packetSet.contains(packetName)) {
            //WynnAdhocClient.LOGGER.temp(packetName);
        }

        if (playPacketSet.contains(packet.getPacketType())) {
            String prefix = bundled ? "(bundled) " : "";
            //WynnAdhocClient.LOGGER.temp(prefix + packet);
        }
    }
}
