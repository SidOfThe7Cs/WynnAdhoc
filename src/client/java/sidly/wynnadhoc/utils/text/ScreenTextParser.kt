package sidly.wynnadhoc.utils.text

import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.Formatting

class ScreenTextParser : TextParser {
    constructor(screen: Screen) : super(screen.title)

    //TextPart(string='󏿭', style={color=white,font=Font[id=minecraft:interface]})
    fun isMountFeeder(): Boolean {
        get(0)?.let {
            if (it.string != "\uDAFF\uDFED\uE058") return false
            if (it.isNotColor(Formatting.WHITE)) return false
            if (!it.isFontPath("interface")) return false
            return true
        }
        return false
    }
}