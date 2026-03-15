package sidly.wynnadhoc.mixin.client.accessors;

import com.wynntils.screens.guildlog.GuildLogHolder;
import com.wynntils.screens.guildlog.GuildLogScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuildLogScreen.class)
public interface GuildLogScreenAccessor {
    @Accessor(value = "holder", remap = false) GuildLogHolder getHolder();
}
