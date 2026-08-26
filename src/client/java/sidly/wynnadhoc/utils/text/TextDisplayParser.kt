package sidly.wynnadhoc.utils.text

import net.minecraft.entity.decoration.DisplayEntity

class TextDisplayParser : TextParser {
    constructor(display: DisplayEntity.TextDisplayEntity) : super(display.text)

    fun isRareMob(): Boolean {
        val part = get(0) ?: return false
        val string = part.string == "\uE02A"
        val color = part.getColor()?.equals(0xCC29CC) ?: false
        val font = part.getFontId()?.path?.equals("common") ?: false
        return string && color && font
    }
}
