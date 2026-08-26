package sidly.wynnadhoc.utils.text

import net.minecraft.text.Style
import net.minecraft.text.StyleSpriteSource
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

class TextPart(val string: String, val style: Style) {

    fun getColorRgb(): Int? {
        return style.color?.rgb
    }

    fun isNotColor(formattingColor: Formatting): Boolean {
        return !isColor(formattingColor)
    }

    fun isColor(formattingColor: Formatting): Boolean {
        return style.color?.equals(TextColor.fromFormatting(formattingColor)) ?: false
    }

    fun getFontId(): Identifier? {
        val font = style.font
        return if (font is StyleSpriteSource.Font) font.id() else null
    }

    fun isEmpty(): Boolean {
        return string.trim().isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return !isEmpty()
    }

    override fun toString(): String {
        return "TextPart(string='$string', style=$style)"
    }
}