package sidly.wynnadhoc.features.c2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.DynamicRegistryManager;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class C2CFriendlyByteBuf extends RegistryByteBuf {
    @Nullable
    private final String sender;
    @Nullable
    private final UUID senderUUID;

    public C2CFriendlyByteBuf(ByteBuf source, DynamicRegistryManager registryAccess, @Nullable String sender, @Nullable UUID senderUUID) {
        super(source, registryAccess);
        this.sender = sender;
        this.senderUUID = senderUUID;
    }

    @Nullable
    public String getSender() {
        return this.sender;
    }

    @Nullable
    public UUID getSenderUUID() {
        return this.senderUUID;
    }
}
