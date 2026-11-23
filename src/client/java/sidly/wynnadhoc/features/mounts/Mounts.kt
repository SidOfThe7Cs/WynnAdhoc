package sidly.wynnadhoc.features.mounts

import com.wynntils.utils.colors.CustomColor
import com.wynntils.utils.render.RenderUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.event.ScreenRenderEvent
import sidly.wynnadhoc.mixin.client.Invoker.ScreenInvoker
import sidly.wynnadhoc.mixin.client.accessors.HandledScreenAccessor
import sidly.wynnadhoc.utils.text.ItemTextParser
import sidly.wynnadhoc.utils.text.ScreenTextParser
import sidly.wynnadhoc.utils.text.TextPart

object Mounts {
    fun onScreenRender(event: ScreenRenderEvent) {
        if (ScreenTextParser(event.screen).isMountFeeder()) {
            (event.screen as? ScreenInvoker)?.let {
                val button = ButtonWidget.builder(Text.literal("Open Mount Color Selector")) { openScreen() }
                    .dimensions(
                        event.screen.width / 2 - 90,
                        event.screen.height / 2 - 145,
                        180,
                        20
                    ).build()

                it.invokeAddDrawableChild(button)
            }
        }

        if (ConfigManager.INSTANCE.selectedMountColors.isEmpty()) return
        val accessor = event.screen as? HandledScreenAccessor ?: return
        (event.screen as? HandledScreen<*>)?.screenHandler?.slots?.forEach { slot ->
            ItemTextParser(slot.stack).getMount()?.let { mount ->
                if (!ConfigManager.INSTANCE.selectedMountColors.anyColorMatch(mount)) {
                    RenderUtils.drawLine(
                        event.context,
                        CustomColor.fromChatFormatting(Formatting.BLACK),
                        accessor.x.toFloat() + slot.x.toFloat(),
                        accessor.y.toFloat() + slot.y.toFloat(),
                        accessor.x.toFloat() + slot.x.toFloat() + 16,
                        accessor.y.toFloat() + slot.y.toFloat() + 16,
                        2f
                    )

                    RenderUtils.drawLine(
                        event.context,
                        CustomColor.fromChatFormatting(Formatting.BLACK),
                        accessor.x.toFloat() + slot.x.toFloat() + 16,
                        accessor.y.toFloat() + slot.y.toFloat(),
                        accessor.x.toFloat() + slot.x.toFloat(),
                        accessor.y.toFloat() + slot.y.toFloat() + 16,
                        2f
                    )
                }
            }
        }
    }

    val mountScreen: MountColorSelectorScreen = MountColorSelectorScreen(null)

    fun openScreen() {
        mountScreen.oldScreen = MinecraftClient.getInstance().currentScreen
        MinecraftClient.getInstance().setScreen(mountScreen)
    }

}

fun Set<MountCore>.anyColorMatch(other: MountCore): Boolean {
    for (mount in this) {
        if (mount.colorMatch(other)) return true
    }
    return false
}

fun TextPart.isMount(): Boolean {
    return this.string == "\uE00C\uE00E\uE014\uE00D\uE013\uDB00\uDC02" &&
            this.isColor(Formatting.BLACK) &&
            this.isFontPath("banner/box")
}