package sidly.wynnadhoc.features.mounts

import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.utils.gui.ButtonContainerScreen

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
        else if (buttonContainer.buttons.isEmpty()) addButton(MountCore())
        else {
            val lastButton = this.buttonContainer.lastButton
            if (lastButton is MountColorSelectorButton) {
                val left = lastButton.leftSelector.textBoxInput
                val right = lastButton.rightSelector.textBoxInput
                if (!right.isEmpty() && !left.isEmpty() && !right.equals("UNKNOWN") && !left.equals("UNKNOWN")) {
                    this.addButton(MountCore())
                }
            }
        }
    }

    fun addButton(data: MountCore) {
        suppressAddButton = true
        buttonContainer.addButton(MountColorSelectorButton(data, Text.literal(""), this))
        suppressAddButton = false
    }
}