package sidly.wynnadhoc.utils.text

import net.minecraft.entity.decoration.DisplayEntity
import net.minecraft.util.Formatting

class TextDisplayParser : TextParser {
    constructor(display: DisplayEntity.TextDisplayEntity) : super(display.text)

    fun isRareMob(): Boolean {
        val part = get(0) ?: return false
        val string = part.string == "\uE02A"
        val color = part.getColorRgb()?.equals(0xCC29CC) ?: false
        val font = part.getFontId()?.path?.equals("common") ?: false
        return string && color && font
    }

    fun isShadowling(): Boolean {
        val part = get(0) ?: return false
        val string = part.string == "Shadowling"
        val color = part.isColor(Formatting.RED)
        return string && color
    }

    fun isBulbCatcher(): Boolean {
        val part = get(0) ?: return false
        val string = part.string == "Bulb Catcher"
        val color = part.isColor(Formatting.RED)
        return string && color
    }

    //TODO make these into an enum
}
