package sidly.wynnadhoc.features

import com.wynntils.services.map.pois.IconPoi
import com.wynntils.services.map.pois.Poi
import com.wynntils.services.map.type.DisplayPriority
import com.wynntils.utils.colors.CommonColors
import com.wynntils.utils.colors.CustomColor
import com.wynntils.utils.mc.type.PoiLocation
import com.wynntils.utils.render.Texture
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import sidly.wynnadhoc.utils.gui.ButtonContainerScreen
import sidly.wynnadhoc.utils.gui.CustomButton
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
    private var lastSelected: List<CustomButton> = mutableListOf()

    private fun addWaypoint(name: String, pos: BlockPos) {
        val existing = currentWaypoints.find { it.location.asLocation().toBlockPos() == pos }
        if (existing != null) {
            if (!existing.name.contains(name)) existing.appendName(" $name")
        } else currentWaypoints.add(MobPoi(name, pos, CommonColors.WHITE))
    }

    fun getWaypoints(): Set<Poi> {
        return currentWaypoints
    }

    class SelectorScreen(oldScreen: Screen) : ButtonContainerScreen(oldScreen) {
        override fun preClose() {
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
        }

        override fun afterInit() {
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

            val textureInfo = super.buttonContainer.backgroundTexture
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
    }

}