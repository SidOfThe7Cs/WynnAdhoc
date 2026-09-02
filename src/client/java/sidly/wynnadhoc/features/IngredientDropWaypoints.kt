package sidly.wynnadhoc.features

import com.wynntils.core.consumers.screens.WynntilsScreen
import com.wynntils.screens.base.widgets.TextInputBoxWidget
import com.wynntils.screens.maps.MainMapScreen
import com.wynntils.services.map.pois.IconPoi
import com.wynntils.services.map.pois.Poi
import com.wynntils.services.map.type.DisplayPriority
import com.wynntils.utils.colors.CommonColors
import com.wynntils.utils.colors.CustomColor
import com.wynntils.utils.mc.type.PoiLocation
import com.wynntils.utils.render.Texture
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Click
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import sidly.wynnadhoc.utils.render.ButtonContainer
import sidly.wynnadhoc.utils.render.TextureInfo
import sidly.wynnadhoc.wapi.ItemDatabase
import sidly.wynnadhoc.wapi.item.WynnItem

object IngredientDropWaypoints {
    class MobPoi(private var name: String, private val pos: BlockPos, private val color: CustomColor) : IconPoi() {
        override fun getIconColor(): CustomColor {
            return color
        }

        override fun getIcon(): Texture {
            return Texture.FIREBALL
        }

        override fun getMinZoomForRender(): Float {
            return 0f
        }

        override fun getLocation(): PoiLocation {
            return PoiLocation(pos.x, pos.y, pos.z)
        }

        override fun getDisplayPriority(): DisplayPriority {
            return DisplayPriority.HIGH
        }

        override fun hasStaticLocation(): Boolean {
            return true
        }

        override fun getName(): String {
            return name
        }

        fun appendName(string: String) {
            name += string
        }
    }

    private val itemMap = mutableMapOf<String, WynnItem>()
    private val currentWaypoints = mutableSetOf<MobPoi>()
    private var lastSelected: List<ButtonContainer.ToggleButton> = mutableListOf()

    private fun addWaypoint(name: String, pos: BlockPos) {
        val existing = currentWaypoints.find { it.location.asLocation().toBlockPos() == pos }
        if (existing != null) {
            if (!existing.name.contains(name)) existing.appendName(" $name")
        } else currentWaypoints.add(MobPoi(name, pos, CommonColors.WHITE))
    }

    fun getWaypoints(): Set<Poi> {
        return currentWaypoints
    }

    class SelectorScreen(private val oldMapScreen: MainMapScreen) :
        WynntilsScreen(Text.literal("Ingredient Mob Search Screen")) {
        private var searchInput: TextInputBoxWidget? = null
        private val textureInfo = TextureInfo(Texture.WAYPOINT_MANAGER_BACKGROUND, 10, 26, 16, 9)
        private val buttonContainer = ButtonContainer(textureInfo, 2, false)

        override fun close() {
            currentWaypoints.clear()
            val selected = buttonContainer.selected
            lastSelected = selected
            selected.forEach { tButton ->
                // TODO Toggle button should have a type thingy like <T> (also add count of waypoints its going to add to button text)
                val item = itemMap[tButton.text.string] ?: return@forEach
                item.droppedBy?.let { dropData ->
                    dropData.forEach { mobData ->
                        mobData?.coords?.forEach { coord ->
                            if (coord != null) {
                                val radius = coord.r
                                addWaypoint("${mobData.name} ($radius)", coord.blockPos)
                            }
                        }
                    }
                }
            }

            MinecraftClient.getInstance().setScreen(oldMapScreen)
        }

        public override fun doInit() {
            addDrawableChild(
                ButtonWidget.Builder(
                    Text.literal("X").setStyle(Style.EMPTY.withColor(Formatting.RED))
                ) { _: ButtonWidget -> close() }
                    .position(
                        (this.translationX + Texture.WAYPOINT_MANAGER_BACKGROUND.width() - 20).toInt(),
                        (this.translationY - 22).toInt()
                    )
                    .size(20, 20)
                    .build()
            )

            searchInput = TextInputBoxWidget(
                (this.translationX + 2).toInt(),
                (this.translationY - 22).toInt(),
                Texture.WAYPOINT_MANAGER_BACKGROUND.width() / 2,
                20,
                { filterString: String? -> buttonContainer.setFilterString(filterString) },
                this,
                searchInput
            )

            addDrawableChild<TextInputBoxWidget?>(searchInput)
            focusedTextInput = searchInput

            addDrawableChild(
                ButtonWidget.Builder(
                    Text.literal("Search Api").setStyle(Style.EMPTY.withColor(Formatting.GREEN))
                ) { _: ButtonWidget -> search() }
                    .position(
                        searchInput!!.x + searchInput!!.width + 10,
                        (this.translationY - 22).toInt()
                    )
                    .size(80, 20)
                    .build()
            )

            val hideAllWidth = textureInfo.drawableWidth / 3
            addDrawableChild(
                ButtonWidget.Builder(
                    Text.literal("Hide All").setStyle(Style.EMPTY.withColor(Formatting.RED))
                ) { _: ButtonWidget -> hideAll() }
                    .position(
                        textureInfo.getDrawableCenterX(this) - hideAllWidth / 2,
                        textureInfo.getY(this) + textureInfo.height() + 2,
                    )
                    .size(hideAllWidth, 20)
                    .build()
            )

            lastSelected.forEach { tButton ->
                buttonContainer.addButton(tButton)
            }
        }

        fun hideAll() {
            buttonContainer.hideAll()
            close()
        }

        fun search() {
            buttonContainer.cleanUnselected()
            val string = searchInput?.textBoxInput ?: return
            val results = ItemDatabase.searchApi(string)
            results.whenComplete { items, throwable ->
                items.forEach { item ->
                    itemMap[item.internalName] = item
                    buttonContainer.addButton(
                        Text.literal(item.internalName),
                        {},
                        20,
                    )
                }
            }
        }

        override fun renderBackground(guiGraphics: DrawContext?, mouseX: Int, mouseY: Int, partialTick: Float) {
            super.renderBackground(guiGraphics, mouseX, mouseY, partialTick)
            buttonContainer.renderBackground(guiGraphics, this)
        }

        override fun mouseScrolled(mouseX: Double, mouseY: Double, deltaX: Double, deltaY: Double): Boolean {
            return buttonContainer.onMouseScrolled(mouseX, mouseY, deltaX, deltaY)
        }

        override fun doRender(guiGraphics: DrawContext?, mouseX: Int, mouseY: Int, partialTick: Float) {
            super.doRender(guiGraphics, mouseX, mouseY, partialTick)
            buttonContainer.render(guiGraphics, mouseX, mouseY, partialTick, this)
        }

        override fun doMouseClicked(event: Click?, isDoubleClick: Boolean): Boolean {
            super.doMouseClicked(event, isDoubleClick)
            return buttonContainer.onMouseClicked(event, isDoubleClick)
        }

        private val translationX: Float
            get() = (this.width - Texture.WAYPOINT_MANAGER_BACKGROUND.width()) / 2f

        private val translationY: Float
            get() = (this.height - Texture.WAYPOINT_MANAGER_BACKGROUND.height()) / 2f
    }

}