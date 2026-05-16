package sidly.wynnadhoc.utils.resource_pack;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.*;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.joml.Vector2i;
import sidly.wynnadhoc.mixin.client.accessors.FontManagerAccessor;
import sidly.wynnadhoc.mixin.client.accessors.FontStorageAccessor;
import sidly.wynnadhoc.mixin.client.accessors.MinecraftClientAccessor;
import sidly.wynnadhoc.wapi.item.WynnItem;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class FontUtils {
    private static final Map<BitmapFont, Identifier> glyphLocations = new HashMap<>();
    private static FontManager fontManager = null;
    private static Map<Identifier, FontStorage> fontStorages = new HashMap<>();

    private static final Map<Identifier, Map<Vector2i, Function<WynnItem.Builder, WynnItem.Builder>>> mappings = new HashMap<>();

    static {

    }

    public static void updateFontStorages(boolean force) {
        if (fontManager == null)
            fontManager = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getFontManager();
        if (fontStorages.isEmpty() || force) fontStorages = ((FontManagerAccessor) fontManager).getFontStorages();
    }

    public static void onBitmapCreate(BitmapFont bitmapFont, Identifier id) {
        glyphLocations.put(bitmapFont, id);
    }

    public static String translate(Text text) {
        StringBuilder processed = new StringBuilder();
        processText(text, processed);
        return processed.toString();
    }

    private static void processText(Text text, StringBuilder builder) {
        // Visit the text to extract characters with their correct styles
        text.visit((style, string) -> {
            processString(string, style, builder);
            return Optional.empty();
        }, Style.EMPTY);

        // Process all siblings
        for (Text sibling : text.getSiblings()) {
            processText(sibling, builder);
        }
    }

    private static void processString(String str, Style style, StringBuilder builder) {
        StyleSpriteSource source = style.getFont();

        // Skip if no font specified or it's not a Font type
        if (!(source instanceof StyleSpriteSource.Font(Identifier fontId))) {
            // Just append the raw string if no special font
            builder.append(str);
            return;
        }

        updateFontStorages(false);

        FontStorage storage = fontStorages.get(fontId);
        if (storage == null) {
            builder.append(str);
            return;
        }

        FontStorageAccessor storageAccessor = (FontStorageAccessor) storage;

        for (int i = 0; i < str.length(); ) {
            int codePoint = str.codePointAt(i);

            Pair<Font, Vector2i> codePointInfo = findFontForCodePoint(storageAccessor, codePoint);

            boolean found = false;
            if (codePointInfo.getLeft() instanceof BitmapFont bitmapFont) {
                Identifier identifier = glyphLocations.get(bitmapFont);
                if (identifier != null && !isIgnoredPath(identifier)) {
                    found = true;
                    Vector2i pos = codePointInfo.getRight();
                    String posStr = pos == null ? "(?)" : pos.toString(NumberFormat.getCompactNumberInstance());
                    FontData cached = FontData.get(identifier, pos);
                    String s = "";
                    if (cached == null) {
                        if (identifier.getPath().startsWith("textures/font/hud/gameplay/default")) {
                            String[] parts = identifier.getPath().split("/");

                            if (parts.length >= 2) {
                                String fileName = parts[parts.length - 1];
                                String dirName = parts[parts.length - 2];

                                // Remove extension from filename
                                int dotIndex = fileName.lastIndexOf('.');
                                String nameWithoutExt = dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;

                                // Take first letter of each word in directory name (handling underscores)
                                String[] dirWords = dirName.split("_");
                                StringBuilder abbr = new StringBuilder();
                                for (String word : dirWords) {
                                    if (!word.isEmpty()) {
                                        abbr.append(word.charAt(0));
                                    }
                                }

                                s = abbr + "_" + nameWithoutExt;
                            } else s = "(" + identifier + "<at" + posStr + ">)";
                        } else s = "(" + identifier + "<at" + posStr + ">)";
                    } else s = cached.getDisplayName();
                    builder.append(s);
                    String pos = codePointInfo.getRight() == null ? "(?)" : codePointInfo.getRight().toString(NumberFormat.getCompactNumberInstance());
                    builder.append("(").append(identifier).append("<at").append(pos).append(">)");
                }
            }
            if (!found) builder.appendCodePoint(codePoint);

            i += Character.charCount(codePoint);
        }
    }

    private static boolean isIgnoredPath(Identifier id) {
        String path = id.getPath();
        return path.equals("ascii") ||
                path.equals("language/wynncraft") ||
                path.endsWith("/ascii") ||
                path.endsWith("ascii.png");
    }

    private static Pair<Font, Vector2i> findFontForCodePoint(FontStorageAccessor storage, int codePoint) {
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
                    return new Pair<>(font, getTexturePos(glyph)); // This is the one Minecraft uses
                }
            }
        }

        return new Pair<>(fallbackFont, null); // No valid glyph found, use first available
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

    public static Vector2i getTexturePos(Glyph glyph) {
        if (glyph instanceof BitmapFont.BitmapFontGlyph bitmap) {
            int xPos = bitmap.x() / bitmap.width();
            int yPos = bitmap.y() / bitmap.height();
            return new Vector2i(xPos, yPos);
        } else return null;
    }
}
