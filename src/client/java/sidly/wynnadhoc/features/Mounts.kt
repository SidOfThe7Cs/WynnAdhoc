package sidly.wynnadhoc.features

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.util.Formatting
import sidly.wynnadhoc.event.ScreenRenderEvent
import sidly.wynnadhoc.mixin.client.accessors.HandledScreenAccessor
import sidly.wynnadhoc.utils.text.ItemTextParser
import sidly.wynnadhoc.utils.text.TextPart

object Mounts {
    fun onScreenRender(event: ScreenRenderEvent) {
        if (event.screen is HandledScreen<*>) {
            (event.screen as HandledScreenAccessor).focusedSlot?.stack?.let { itemStack ->
                ItemTextParser(itemStack).getMount()?.let { mount ->

                }
            }
        }
    }

    fun TextPart.isMount(): Boolean {
        return this.string.equals("\uE00C\uE00E\uE014\uE00D\uE013\uDB00\uDC02") &&
                this.isColor(Formatting.BLACK) &&
                this.isFontPath("banner/box")
    }

    class Mount {
        var primaryColor: PrimaryColor = PrimaryColor.UNKNOWN
        var secondaryColor: SecondaryColor = SecondaryColor.UNKNOWN

        companion object {
            fun fromParser(parser: ItemTextParser): Mount? {
                val findIndex = parser.findIndex({ part -> part.isMount() })
                if (findIndex == -1) return null
                val mount = Mount()
                val colorLine = parser.find({ part ->
                    mount.primaryColor = PrimaryColor.UNKNOWN
                    mount.secondaryColor = SecondaryColor.UNKNOWN
                    val split = part.string.trim().split("-")
                    if (split.size != 2) return@find false
                    mount.primaryColor = PrimaryColor.fromString(split[0])
                    if (mount.primaryColor == PrimaryColor.UNKNOWN) return@find false
                    mount.secondaryColor = SecondaryColor.fromString(split[1])
                    return@find mount.secondaryColor != SecondaryColor.UNKNOWN
                }, findIndex)
                return if (colorLine == null) null else mount
            }
        }
    }

    enum class PrimaryColor {
        UNKNOWN,
        Crimson,
        Dust,
        Amber,
        Emerald,
        Cobalt,
        Dusk,
        Plum,
        Sable,
        Ash,
        Albino;

        companion object {
            fun fromString(s: String): PrimaryColor {
                entries.forEach { if (it.name == s) return it }
                return UNKNOWN
            }
        }
    }

    enum class SecondaryColor {
        UNKNOWN,
        Blood,
        Rose,
        Tawny,
        Moss,
        Royal,
        Misty,
        Maroon,
        Raven,
        Sage,
        Bleach;

        companion object {
            fun fromString(s: String): SecondaryColor {
                entries.forEach { if (it.name == s) return it }
                return UNKNOWN
            }
        }
    }
}