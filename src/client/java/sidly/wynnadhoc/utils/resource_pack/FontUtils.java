package sidly.wynnadhoc.utils.resource_pack;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.*;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector2i;
import sidly.wynnadhoc.mixin.client.accessors.FontManagerAccessor;
import sidly.wynnadhoc.mixin.client.accessors.FontStorageAccessor;
import sidly.wynnadhoc.mixin.client.accessors.MinecraftClientAccessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FontUtils {
    private static final Map<BitmapFont, Identifier> glyphLocations = new HashMap<>();
    private static FontManager fontManager = null;
    private static Map<Identifier, FontStorage> fontStorages = new HashMap<>();
    private static final Set<String> ignoredPaths = Set.of(
            "textures/font/ascii.png",
            "textures/font/language/wynncraft.png"
    );

    public static void updateFontStorages(boolean force) {
        if (fontManager == null)
            fontManager = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getFontManager();
        if (fontStorages.isEmpty() || force) fontStorages = ((FontManagerAccessor) fontManager).getFontStorages();
    }

    public static void onBitmapCreate(BitmapFont bitmapFont, Identifier id) {
        glyphLocations.put(bitmapFont, id);
    }

    public static String translate(Text text) {
        String original = text.getString();
        StringBuilder processed = new StringBuilder();
        StyleSpriteSource source = text.getStyle().getFont();

        if (source instanceof StyleSpriteSource.Font(Identifier id)) {
            updateFontStorages(false);

            FontStorage storage = fontStorages.get(id);
            FontStorageAccessor storageAccessor = (FontStorageAccessor) storage;
            GlyphProvider provider = storage.getGlyphs(false);

            for (int i = 0; i < original.length(); ) {
                int codePoint = original.codePointAt(i);
                BakedGlyph baked = provider.get(codePoint);

                processed.appendCodePoint(codePoint);


                Font usedFont = findFontForCodePoint(storageAccessor, codePoint);

                if (usedFont instanceof BitmapFont bitmapFont) {
                    Identifier identifier = glyphLocations.get(bitmapFont);
                    if (identifier != null && !ignoredPaths.contains(identifier.getPath())) {
                        processed.append("(").append(identifier).append(")");
                    }
                }

                i += Character.charCount(codePoint);
            }
        }

        return processed.toString();
    }

    private static Font findFontForCodePoint(FontStorageAccessor storage, int codePoint) {
        // This replicates FontStorage.findGlyph() logic
        List<Font> fonts = storage.getAvailableFonts();
        Font fallbackFont = null;

        for (Font font : fonts) {
            Glyph glyph = font.getGlyph(codePoint);
            if (glyph != null) {
                if (fallbackFont == null) {
                    fallbackFont = font;
                }
                if (!isAdvanceInvalid(glyph.getMetrics())) {
                    return font; // This is the one Minecraft uses
                }
            }
        }

        return fallbackFont; // No valid glyph found, use first available
    }

    private static boolean isAdvanceInvalid(GlyphMetrics glyph) {
        float f = glyph.getAdvance(false);
        if (!(f < 0.0F) && !(f > 32.0F)) {
            float g = glyph.getAdvance(true);
            return g < 0.0F || g > 32.0F;
        } else {
            return true;
        }
    }

    public static Vector2i getTexturePos(BitmapFont.BitmapFontGlyph bitmap) {
        int xPos = bitmap.x() / bitmap.width();
        int yPos = bitmap.y() / bitmap.height();
        return new Vector2i(xPos, yPos);
    }
}
