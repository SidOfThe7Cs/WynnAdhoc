package sidly.wynnadhoc.utils.text

import net.minecraft.text.PlainTextContent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextContent

abstract class TextParser {
    private val parts: MutableList<TextPart> = mutableListOf()

    constructor()
    constructor(text: Text) : this() {
        extractParts(text, Style.EMPTY)
    }

    constructor(text: List<Text>) : this() {
        text.forEach { extractParts(it, Style.EMPTY) }
    }

    fun find(condition: (TextPart) -> Boolean, startIndex: Int = 0): TextPart? {
        return parts.subList(startIndex, parts.size).firstOrNull { condition(it) }
    }

    fun findIndex(condition: (TextPart) -> Boolean, startIndex: Int = 0): Int {
        return parts.subList(startIndex, parts.size).indexOfFirst { condition(it) }
    }

    fun get(index: Int): TextPart? {
        if (index < 0 || index >= parts.size) return null
        return parts[index]
    }

    fun get(): List<TextPart> {
        return parts
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

}