package sidly.wynnadhoc.utils.gui;

import com.wynntils.core.consumers.screens.WynntilsScreen;
import com.wynntils.screens.base.widgets.TextInputBoxWidget;
import com.wynntils.utils.render.Texture;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sidly.wynnadhoc.utils.render.TextureInfo;

public class ButtonContainerScreen extends WynntilsScreen {
    protected TextInputBoxWidget searchInput;
    protected ButtonContainer buttonContainer;

    public ButtonContainerScreen() {
        super(Text.literal("WynnAdhoc ButtonContainer Screen"));
        buttonContainer = new ButtonContainer(
                new TextureInfo(Texture.WAYPOINT_MANAGER_BACKGROUND,
                        10,
                        26,
                        16,
                        9
                ), 2, true);
    }

    public ButtonContainerScreen(TextureInfo textureInfo, int spacing, boolean filterSearch) {
        this();
        buttonContainer = new ButtonContainer(textureInfo, spacing, filterSearch);
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

        afterInit();
    }

    public void afterInit() {

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
