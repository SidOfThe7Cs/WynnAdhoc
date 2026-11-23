package sidly.wynnadhoc.utils.text

import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import sidly.wynnadhoc.features.mounts.MountCore
import sidly.wynnadhoc.utils.ItemUtils

class ItemTextParser : TextParser {
    constructor(text: List<Text>) : super(text)
    constructor(stack: ItemStack) : super(ItemUtils.getTooltip(stack))

    fun isIngredientPouch(): Boolean {
        val part = get(0) ?: return false
        return part.string == "Ingredient Pouch" && part.isColor(Formatting.GOLD)
    }

    fun getMount(): MountCore? {
        return MountCore.fromParser(this)
    }
}