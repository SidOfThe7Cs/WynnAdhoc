package sidly.wynnadhoc.features.c2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.DynamicRegistryManager;
import org.jspecify.annotations.Nullable;

public class C2CFriendlyByteBuf extends RegistryByteBuf {
    @Nullable
    private final String sender;

    public C2CFriendlyByteBuf(ByteBuf source, DynamicRegistryManager registryAccess, @Nullable String sender) {
        super(source, registryAccess);
        this.sender = sender;
    }

    @Nullable
    public String getSender() {
        return this.sender;
    }
}
