package sidly.wynnadhoc.mixin.client.accessors;

import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(FontStorage.class)
public interface FontStorageAccessor {
    @Accessor("availableFonts")
    List<Font> getAvailableFonts();

    @Invoker("findGlyph")
    FontStorage.GlyphPair invokeFindGlyph(int codePoint);
}
