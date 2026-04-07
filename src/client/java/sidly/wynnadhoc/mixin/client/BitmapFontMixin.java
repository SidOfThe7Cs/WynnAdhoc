package sidly.wynnadhoc.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.font.BitmapFont;
import net.minecraft.client.font.GlyphContainer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import sidly.wynnadhoc.utils.resource_pack.FontUtils;

@Mixin(BitmapFont.Loader.class)
public class BitmapFontMixin {
    @WrapOperation(
            method = "load",
            at = @At(value = "NEW", target = "net/minecraft/client/font/BitmapFont")
    )
    public BitmapFont onCreate(
            NativeImage image,
            GlyphContainer<BitmapFont.BitmapFontGlyph> glyphs,
            Operation<BitmapFont> original,
            @Local(name = "identifier") Identifier id
    ) {
        BitmapFont font = original.call(image, glyphs);
        FontUtils.onBitmapCreate(font, id);
        return font;
    }
}