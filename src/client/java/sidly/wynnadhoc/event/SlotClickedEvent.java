package sidly.wynnadhoc.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

public class SlotClickedEvent extends Event<SlotClickedEvent> {
    public ScreenHandler handler;
    public int slotIndex;
    public int button;
    public SlotActionType actionType;
    public PlayerEntity player;

    public SlotClickedEvent(ScreenHandler handler, int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        this.handler = handler;
        this.slotIndex = slotIndex;
        this.button = button;
        this.actionType = actionType;
        this.player = player;
        this.fire();
    }
}
