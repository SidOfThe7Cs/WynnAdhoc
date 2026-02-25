package sidly.wynnadhoc.event;

import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class TextDisplaySyncEvent extends Event<TextDisplaySyncEvent> {
    public DisplayEntity.TextDisplayEntity textDisplay;
    public Text text;
    public String string;
    public Vec3d pos;
    public BlockPos blockPos;

    public TextDisplaySyncEvent(DisplayEntity.TextDisplayEntity textDisplay) {
        this.textDisplay = textDisplay;
        this.text = textDisplay.getText();
        this.string = text.getString();
        this.pos = textDisplay.getEntityPos();
        this.blockPos = textDisplay.getBlockPos();
        this.fire();
    }
}
