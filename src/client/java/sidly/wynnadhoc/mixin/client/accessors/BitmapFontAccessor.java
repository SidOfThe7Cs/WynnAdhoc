package sidly.wynnadhoc.mixin.client.accessors;

import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.font.GlyphContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BitmapFont.class)
public interface BitmapFontAccessor {
    @Accessor("glyphs")
    GlyphContainer<BitmapFont.BitmapFontGlyph> getGlyphs();
}
