package sidly.wynnadhoc.features.chests;

import com.wynntils.core.consumers.screens.WynntilsScreen;
import com.wynntils.screens.base.widgets.TextInputBoxWidget;
import com.wynntils.screens.maps.MainMapScreen;
import com.wynntils.services.map.pois.IconPoi;
import com.wynntils.services.map.pois.Poi;
import com.wynntils.services.map.type.DisplayPriority;
import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.colors.CustomColor;
import com.wynntils.utils.mc.type.PoiLocation;
import com.wynntils.utils.render.Texture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import sidly.wynnadhoc.utils.render.ButtonContainer;
import sidly.wynnadhoc.utils.render.TextureInfo;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomWynntillsMapWaypoints {
    public static class ChestPoi extends IconPoi {
        //private final Texture icon;
        private final BlockPos pos;
        private final CustomColor color;
        private final String name;

        public ChestPoi(String name, BlockPos pos, CustomColor color) {
            this.pos = pos;
            this.color = color;
            this.name = name;
        }

        @Override
        protected CustomColor getIconColor() {
            return color;
        }

        @Override
        public Texture getIcon() {
            return Texture.CHEST_T1;
        }

        @Override
        protected float getMinZoomForRender() {
            return 0;
        }

        @Override
        public PoiLocation getLocation() {
            return new PoiLocation(pos.getX(), pos.getY(), pos.getZ());
        }

        @Override
        public DisplayPriority getDisplayPriority() {
            return DisplayPriority.HIGH;
        }

        @Override
        public boolean hasStaticLocation() {
            return true;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static class SelectorScreen extends WynntilsScreen {
        private final MainMapScreen oldMapScreen;
        private TextInputBoxWidget searchInput;
        private final ButtonContainer buttonContainer = new ButtonContainer(
                new TextureInfo(Texture.WAYPOINT_MANAGER_BACKGROUND,
                        10,
                        26,
                        16,
                        9
                ), 2);

        public SelectorScreen(MainMapScreen oldScreen) {
            super(Text.literal("Chest Level Selector Screen"));
            this.oldMapScreen = oldScreen;
        }

        @Override
        public void close() {
            MinecraftClient.getInstance().setScreen(oldMapScreen);
        }

        @Override
        public void doInit() {
            addDrawableChild(
                    new ButtonWidget.Builder(Text.literal("X").setStyle(Style.EMPTY.withColor(Formatting.RED)), (button) -> close())
                            .position((int) (getTranslationX() + Texture.WAYPOINT_MANAGER_BACKGROUND.width() - 20), (int) (getTranslationY() - 22))
                            .size(20, 20)
                            .build());

            searchInput = new TextInputBoxWidget(
                    (int) (getTranslationX() + 2),
                    (int) (getTranslationY() - 22),
                    Texture.WAYPOINT_MANAGER_BACKGROUND.width() / 2,
                    20,
                    buttonContainer::setFilterString,
                    this,
                    searchInput
            );

            addDrawableChild(searchInput);
            setFocusedTextInput(searchInput);

            buttonContainer.addButton(Text.literal("Hide All"),
                    (b) -> {
                        selectedLvl = -1;
                        close();
                    }, 20);


            buttonContainer.addButton(Text.literal("Show All"),
                    (b) -> {
                        selectedLvl = -2;
                        close();
                    }, 20);

            for (int i = 1; i <= 120; i += 5) {
                buttonContainer.addButton(Text.literal("Select Lvl " + i + " - " + (i + 4)),
                        (b) -> {
                            selectChestLvlRange(b);
                            close();
                        }, 20);
            }
        }

        @Override
        public void renderBackground(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
            buttonContainer.renderBackground(guiGraphics, this);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
            return buttonContainer.onMouseScrolled(mouseX, mouseY, deltaX, deltaY);
        }

        @Override
        public void doRender(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.doRender(guiGraphics, mouseX, mouseY, partialTick);
            buttonContainer.render(guiGraphics, mouseX, mouseY, partialTick, this);
        }

        @Override
        public boolean doMouseClicked(Click event, boolean isDoubleClick) {
            super.doMouseClicked(event, isDoubleClick);
            return buttonContainer.onMouseClicked(event, isDoubleClick);
        }

        private float getTranslationX() {
            return (this.width - Texture.WAYPOINT_MANAGER_BACKGROUND.width()) / 2f;
        }

        private float getTranslationY() {
            return (this.height - Texture.WAYPOINT_MANAGER_BACKGROUND.height()) / 2f;
        }
    }

    private final static Pattern BUTTON_LVL_REGEX = Pattern.compile("Select Lvl (\\d+).*");
    private static int selectedLvl = -1;

    public static List<Poi> getSelectedPois() {
        List<Poi> pois = new ArrayList<>();
        if (selectedLvl == -1) return pois;

        Map<@NotNull BlockPos, @NotNull ChestDataCache> chestDataCache = ChestTracker.INSTANCE.getChestDataCache();
        for (Map.Entry<BlockPos, ChestDataCache> entry : chestDataCache.entrySet()) {
            Map<Integer, Integer> itemPercents = entry.getValue().getItemPercents();
            if (selectedLvl == -2) { // show all
                Optional<Map.Entry<Integer, Integer>> highest = itemPercents.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue));
                if (highest.isEmpty()) continue;
                int lvl = highest.get().getKey();
                ChestPoi chestPoi = new ChestPoi(lvl + " - " + (lvl + 4) + " " + highest.get().getValue() + "%", entry.getKey(), CommonColors.WHITE);
                pois.add(chestPoi);
            } else {
                Integer percent = itemPercents.getOrDefault(selectedLvl, null);
                if (percent == null) continue;
                CustomColor color = CommonColors.RED;
                if (percent > 15) color = CommonColors.YELLOW;
                if (percent > 40) color = CommonColors.GREEN;
                ChestPoi chestPoi = new ChestPoi(selectedLvl + " - " + (selectedLvl + 4) + " " + percent + "%", entry.getKey(), color);
                pois.add(chestPoi);
            }
        }

        return pois;
    }

    public static void selectChestLvlRange(ButtonWidget button) {
        Matcher matcher = BUTTON_LVL_REGEX.matcher(button.getMessage().getString());
        if (matcher.matches()) {
            selectedLvl = Integer.parseInt(matcher.group(1));
        }
    }
}
