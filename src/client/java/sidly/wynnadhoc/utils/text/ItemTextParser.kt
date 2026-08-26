package sidly.wynnadhoc.utils.text

import net.minecraft.text.Text
import net.minecraft.util.Formatting

class ItemTextParser : TextParser {
    constructor(text: List<Text>) : super(text)

    fun isIngredientPouch(): Boolean {
        val part = get(0) ?: return false
        return part.string == "Ingredient Pouch" && part.isColor(Formatting.GOLD)
    }
}