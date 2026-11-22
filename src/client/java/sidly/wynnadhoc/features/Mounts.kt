package sidly.wynnadhoc.features

import com.wynntils.screens.base.widgets.TextInputBoxWidget
import com.wynntils.utils.colors.CustomColor
import com.wynntils.utils.render.RenderUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Click
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.joml.Vector2d
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.event.ScreenRenderEvent
import sidly.wynnadhoc.features.Mounts.Mount
import sidly.wynnadhoc.mixin.client.accessors.HandledScreenAccessor
import sidly.wynnadhoc.utils.GuiUtils
import sidly.wynnadhoc.utils.gui.ButtonContainer
import sidly.wynnadhoc.utils.gui.ButtonContainerScreen
import sidly.wynnadhoc.utils.gui.CustomButton
import sidly.wynnadhoc.utils.text.ItemTextParser
import sidly.wynnadhoc.utils.text.TextPart
import java.awt.Color

object Mounts {
    fun onScreenRender(event: ScreenRenderEvent) {
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

    class Mount() {
        var primaryColor: PrimaryColor = PrimaryColor.UNKNOWN
        var secondaryColor: SecondaryColor = SecondaryColor.UNKNOWN

        constructor(primaryColor: PrimaryColor, secondaryColor: SecondaryColor) : this() {
            this.primaryColor = primaryColor
            this.secondaryColor = secondaryColor
        }

        fun colorMatch(other: Mount): Boolean {
            if (primaryColor == PrimaryColor.UNKNOWN || secondaryColor == SecondaryColor.UNKNOWN) return false

            var primaryMatch = false
            var secondaryMatch = false

            if (this.primaryColor == other.primaryColor || this.primaryColor == PrimaryColor.ANY || other.primaryColor == PrimaryColor.ANY) {
                primaryMatch = true
            }

            if (this.secondaryColor == other.secondaryColor || this.secondaryColor == SecondaryColor.ANY || other.secondaryColor == SecondaryColor.ANY) {
                secondaryMatch = true
            }

            return primaryMatch && secondaryMatch
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            if (other !is Mount) return false

            if (primaryColor != other.primaryColor) return false
            if (secondaryColor != other.secondaryColor) return false

            return true
        }

        override fun hashCode(): Int {
            var result = primaryColor.hashCode()
            result = 31 * result + secondaryColor.hashCode()
            return result
        }


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

    enum class PrimaryColor : MountColor {
        UNKNOWN,
        ANY,
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

    enum class SecondaryColor : MountColor {
        UNKNOWN,
        ANY,
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

    interface MountColor {
        val name: String
    }

    val mountScreen: MountColorSelectorScreen = MountColorSelectorScreen(null)

    fun openScreen() {
        mountScreen.oldScreen = MinecraftClient.getInstance().currentScreen
        MinecraftClient.getInstance().setScreen(mountScreen)
    }

    class MountColorSelectorScreen(oldScreen: Screen?) : ButtonContainerScreen(oldScreen) {
        private var suppressAddButton = false

        override fun preClose() {
            val saveData = ConfigManager.INSTANCE.selectedMountColors
            saveData.clear()
            buttonContainer.buttons.stream().forEach { button ->
                if (button is MountColorSelectorButton) {
                    val mount = button.toMount()
                    if (mount.primaryColor != PrimaryColor.UNKNOWN && mount.secondaryColor != SecondaryColor.UNKNOWN) {
                        saveData.add(button.toMount())
                    }
                }
            }
            buttonContainer.buttons.clear()
        }

        override fun afterInit() {
            suppressAddButton = true
            ConfigManager.INSTANCE.selectedMountColors.forEach { addButton(it) }
            suppressAddButton = false
            checkAddButton()
        }

        fun checkAddButton() {
            if (suppressAddButton) return
            else if (buttonContainer.buttons.isEmpty()) addButton(Mount())
            else {
                val lastButton = this.buttonContainer.lastButton
                if (lastButton is MountColorSelectorButton) {
                    val left = lastButton.leftSelector.textBoxInput
                    val right = lastButton.rightSelector.textBoxInput
                    if (!right.isEmpty() && !left.isEmpty() && !right.equals("UNKNOWN") && !left.equals("UNKNOWN")) {
                        this.addButton(Mount())
                    }
                }
            }
        }

        fun addButton(data: Mount) {
            suppressAddButton = true
            buttonContainer.addButton(MountColorSelectorButton(data, Text.literal(""), this))
            suppressAddButton = false
        }
    }

    class MountColorSelectorButton(text: Text, val screen: MountColorSelectorScreen) : CustomButton(text) {
        constructor(data: Mount, text: Text, screen: MountColorSelectorScreen) : this(text, screen) {
            if (data.primaryColor != PrimaryColor.UNKNOWN) {
                leftSelector.textBoxInput = data.primaryColor.name
            }
            if (data.secondaryColor != SecondaryColor.UNKNOWN) {
                rightSelector.textBoxInput = data.secondaryColor.name
            }
        }

        enum class Operator {
            OR,
            AND,
        }

        var suggestions: MutableSet<MountColor> = PrimaryColor.entries.toMutableSet()
        var suggestionRenderOrigin: Vector2d? = null

        var operator: Operator = Operator.AND
            set(operator) {
                field = operator
                middleSelector.message = Text.literal(operator.name)
            }

        var middleSelector: ButtonWidget = ButtonWidget.builder(Text.literal(operator.name), this::onMiddleButtonPress)
            .size(screen.buttonContainer.backgroundTexture.drawableWidth / 6, 20)
            .build()

        var leftSelector: TextInputBoxWidget = TextInputBoxWidget(
            0,
            0,
            screen.buttonContainer.backgroundTexture.drawableWidth / 3,
            20,
            this::onLeftInputUpdate,
            screen
        )

        var rightSelector: TextInputBoxWidget = TextInputBoxWidget(
            0,
            0,
            screen.buttonContainer.backgroundTexture.drawableWidth / 3,
            20,
            this::onRightInputUpdate,
            screen
        )

        fun onMiddleButtonPress(button: ButtonWidget) {
//            if (operator == Operator.OR) {
//                operator = Operator.AND
//            } else operator = Operator.OR
        }

        fun onLeftInputUpdate(newInput: String) {
            onInputUpdate()
            PrimaryColor.entries.forEach { entry ->
                addSuggestion(newInput, entry)
            }
        }

        fun onRightInputUpdate(newInput: String) {
            onInputUpdate()
            SecondaryColor.entries.forEach { entry ->
                addSuggestion(newInput, entry)
            }
        }

        private fun onInputUpdate() {
            suggestions.clear()
            screen.checkAddButton()
        }

        private fun addSuggestion(newInput: String, entry: MountColor) {
            val name = entry.name.lowercase()
            val input = newInput.lowercase()
            if (name == input) return
            if (!name.startsWith(input)) return
            suggestions.add(entry)
        }

        override fun onClick(click: Click, doubled: Boolean) {
            getHoveredSuggestion()?.name?.let { screen.focusedTextInput.textBoxInput = it }

            if (middleSelector.isHovered) {
                middleSelector.onClick(click, doubled)
            } else if (leftSelector.isHovered) {
                onLeftInputUpdate(leftSelector.message.string)
                leftSelector.onClick(click, doubled)
                screen.focusedTextInput = leftSelector
            } else if (rightSelector.isHovered) {
                onRightInputUpdate(rightSelector.message.string)
                rightSelector.onClick(click, doubled)
                screen.focusedTextInput = rightSelector
            }
        }

        override fun isVisible(): Boolean {
            return true
        }

        override fun isHovered(): Boolean {
            return if (leftSelector.isHovered) true
            else if (rightSelector.isHovered) true
            else if (middleSelector.isHovered) true
            else if (getHoveredSuggestion() != null) true
            else false
        }

        private fun getHoveredSuggestion(): MountColor? {
            suggestionRenderOrigin?.let { origin ->
                val tr = MinecraftClient.getInstance().textRenderer
                var yOffset = 0
                suggestions.forEach { suggestion ->
                    val text = Text.literal(suggestion.name)
                    val x2 = origin.x + tr.getWidth(text)
                    if (GuiUtils.isMouseWithinBound(
                            origin.x,
                            origin.y + yOffset,
                            x2,
                            origin.y + tr.fontHeight + yOffset
                        )
                    ) {
                        return suggestion
                    }
                    yOffset += tr.fontHeight
                }
            }
            return null
        }

        override fun render(
            context: DrawContext,
            mouseX: Int,
            mouseY: Int,
            partialTick: Float,
            parent: Screen,
            container: ButtonContainer,
            yOffset: Int
        ): Int {
            val bt = container.backgroundTexture
            val origin = bt.getDrawableX(parent)
            //val tr = MinecraftClient.getInstance().textRenderer
            suggestionRenderOrigin = null

            var xPos: Int = origin + container.spacing
            val yPos = bt.getDrawableY(parent) + yOffset
            leftSelector.setPosition(xPos, yPos)
            leftSelector.render(context, mouseX, mouseY, partialTick)
            if (leftSelector.isFocused) {
                val dropDownYPos = yPos + container.spacing + leftSelector.height
                suggestionRenderOrigin = Vector2d(xPos.toDouble(), dropDownYPos.toDouble())
            }

            xPos = origin + bt.drawableWidth / 2 - middleSelector.getWidth() / 2
            middleSelector.setPosition(xPos, yPos)
            middleSelector.render(context, mouseX, mouseY, partialTick)

            xPos = origin + bt.drawableWidth - (rightSelector.getWidth() + container.spacing)
            rightSelector.setPosition(xPos, yPos)
            rightSelector.render(context, mouseX, mouseY, partialTick)
            if (rightSelector.isFocused) {
                val dropDownYPos = yPos + container.spacing + leftSelector.height
                suggestionRenderOrigin = Vector2d(xPos.toDouble(), dropDownYPos.toDouble())
            }

            return leftSelector.getHeight() + container.spacing
        }

        override fun renderOverlay(
            context: DrawContext,
            mouseX: Int,
            mouseY: Int,
            partialTick: Float,
            parent: Screen?,
            container: ButtonContainer,
            yOffset: Int
        ) {
            renderSuggestions(context)
        }

        private fun renderSuggestions(
            context: DrawContext
        ) {
            suggestionRenderOrigin?.let { origin ->
                var yPos = origin.y.toInt()
                val scissor = context.scissorStack.peekLast()
                context.disableScissor()
                val tr = MinecraftClient.getInstance().textRenderer
                suggestions.forEach { suggestion ->
                    val text = Text.literal(suggestion.name)
                    context.drawText(tr, text, origin.x.toInt(), yPos, Color.GREEN.rgb, true)
                    yPos += tr.fontHeight
                }
                if (scissor != null) {
                    context.enableScissor(scissor.left, scissor.top, scissor.right, scissor.bottom)
                }
            }
        }

        fun toMount(): Mount {
            val primary = PrimaryColor.fromString(leftSelector.textBoxInput)
            val secondary = SecondaryColor.fromString(rightSelector.textBoxInput)
            return Mount(primary, secondary)
        }
    }
}

fun Set<Mount>.anyColorMatch(other: Mount): Boolean {
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