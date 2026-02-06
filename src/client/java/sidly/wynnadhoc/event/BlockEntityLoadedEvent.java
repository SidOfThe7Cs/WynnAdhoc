package sidly.wynnadhoc.event;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;

public class BlockEntityLoadedEvent extends Event<BlockEntityLoadedEvent> {
    public BlockEntity blockEntity;
    public ClientWorld clientWorld;

    public BlockEntityLoadedEvent(BlockEntity blockEntity, ClientWorld clientWorld) {
        this.blockEntity = blockEntity;
        this.clientWorld = clientWorld;
        this.fire();
    }
}
