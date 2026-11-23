package sidly.wynnadhoc.features.mounts

import com.wynntils.screens.base.widgets.TextInputBoxWidget
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Click
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import org.joml.Vector2d
import sidly.wynnadhoc.utils.GuiUtils
import sidly.wynnadhoc.utils.gui.ButtonContainer
import sidly.wynnadhoc.utils.gui.CustomButton
import java.awt.Color

class MountColorSelectorButton(text: Text, val screen: MountColorSelectorScreen) : CustomButton(text) {
    constructor(data: MountCore, text: Text, screen: MountColorSelectorScreen) : this(text, screen) {
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

    fun toMount(): MountCore {
        val primary = PrimaryColor.fromString(leftSelector.textBoxInput)
        val secondary = SecondaryColor.fromString(rightSelector.textBoxInput)
        return MountCore(primary, secondary)
    }
}