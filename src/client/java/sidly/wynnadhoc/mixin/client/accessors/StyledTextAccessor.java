package sidly.wynnadhoc.mixin.client.accessors;

import com.wynntils.core.text.StyledText;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(StyledText.class)
public interface StyledTextAccessor {
    @Accessor("clickEvents")
    List<ClickEvent> getClickEvents();

    @Accessor("hoverEvents")
    List<HoverEvent> getHoverEvents();
}
