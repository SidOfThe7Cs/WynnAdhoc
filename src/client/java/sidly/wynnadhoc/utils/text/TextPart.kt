package sidly.wynnadhoc.utils.text

import net.minecraft.text.Style
import net.minecraft.text.StyleSpriteSource
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import sidly.wynnadhoc.utils.text.TextPart.Companion.WYNN_CHAT_NEWLINE

class TextPart(val string: String, val style: Style) {

    fun matches(condition: (TextPart) -> Boolean): Boolean {
        return condition(this)
    }

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

    fun getFontPath(): String? {
        return getFontId()?.path
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

    companion object {
        val WYNN_CHAT_NEWLINE = "\uDAFF\uDFFC\uE001\uDB00\uDC06"
    }
}

fun MutableList<TextPart>.getString(): String {
    val sb = StringBuilder()
    this.filter {
        it.string != WYNN_CHAT_NEWLINE
    }.forEach {
        sb.append(it.string)
    }
    return sb.toString()
}

fun MutableList<TextPart>.getStringNoSpaces(): String {
    val sb = StringBuilder()
    this.filter {
        it.string != WYNN_CHAT_NEWLINE
    }.forEach {
        sb.append(it.string.trim())
    }
    return sb.toString()
}