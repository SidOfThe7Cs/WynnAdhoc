package sidly.wynnadhoc.utils.text

import net.minecraft.text.*
import net.minecraft.util.Identifier

abstract class TextParser {
    protected val parts: MutableList<TextPart> = mutableListOf()

    constructor()
    constructor(text: Text) : this() {
        extractParts(text, Style.EMPTY)
    }

    fun find(condition: (TextPart) -> Boolean, startIndex: Int = 0): Int {
        return parts.subList(startIndex, parts.size).indexOfFirst { condition(it) }
    }

    private fun extractParts(text: Text, parentStyle: Style) {
        val style = text.style.withParent(parentStyle)
        val content = text.content
        if (content != PlainTextContent.EMPTY) {
            val string = getTextContentString(content)
            string.split('\n').forEach { p ->
                parts.add(TextPart(p, style))
            }
        }
        text.siblings.forEach { sibling ->
            extractParts(sibling, style)
        }
    }

    private fun getTextContentString(content: TextContent): String {
        if (content is PlainTextContent.Literal) return content.string
        else return content.toString()
    }

    class TextPart(val string: String, val style: Style) {

        fun getColor(): Int? {
            return style.color?.rgb
        }

        fun getFontId(): Identifier? {
            val font = style.font
            return if (font is StyleSpriteSource.Font) font.id() else null
        }

        override fun toString(): String {
            return "TextPart(string='$string', style=$style)"
        }
    }
}