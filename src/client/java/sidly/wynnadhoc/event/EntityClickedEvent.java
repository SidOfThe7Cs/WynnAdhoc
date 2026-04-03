package sidly.wynnadhoc.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

public class EntityClickedEvent extends Event<EntityClickedEvent> {
    public PlayerEntity player;
    public World world;
    public Hand hand;
    public Entity entity;
    public EntityHitResult hitResult;

    public EntityClickedEvent(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        this.player = playerEntity;
        this.world = world;
        this.hand = hand;
        this.entity = entity;
        this.hitResult = entityHitResult;
        this.fire();
    }

    public static ActionResult onEntityClicked(PlayerEntity playerEntity, World world, Hand hand, Entity entity, @Nullable EntityHitResult entityHitResult) {
        new EntityClickedEvent(playerEntity, world, hand, entity, entityHitResult);
        return ActionResult.PASS;
    }
}
