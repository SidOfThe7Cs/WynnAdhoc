package sidly.wynnadhoc.config.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.GuiConfig;
import sidly.wynnadhoc.event.KeyboardEvent;

import java.awt.*;

public class DraggableHudElementScreen extends Screen {
    private static final GuiConfig config = ConfigManager.INSTANCE.config.gui;

    private static HudElement draggingElement = null;
    private static String desc = "";
    private static double mouseXGlobal = 0;
    private static double mouseYGlobal = 0;
    private static boolean sameKeyPress = false;

    public static void onKeyPressed(KeyboardEvent event) {
        if (event.action == 1 && event.key == config.getGuiEditorKeybind() && MinecraftClient.getInstance().currentScreen == null) {
            MinecraftClient.getInstance().setScreen(new DraggableHudElementScreen());
            sameKeyPress = true;
        }
    }

    public DraggableHudElementScreen() {
        super(Text.of("Gui Editor"));
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (sameKeyPress) sameKeyPress = false;
        else if (input.isEscape() || config.getGuiEditorKeybind() == input.getKeycode()) {
            MinecraftClient.getInstance().setScreen(null);
            ConfigManager.INSTANCE.save();
            return true;
        }

        return super.keyPressed(input);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == 0) { // Left click
            for (HudElement e : HudElementManager.getHudElements()) {
                if (e.isHovering(click.x(), click.y())) {
                    draggingElement = e;
                    e.onMouseClicked(click, doubled);
                    break;
                }
            }
        }
        return super.mouseClicked(click, doubled);
    }


    @Override
    public boolean mouseReleased(Click click) {
        if (click.button() == 0) { // Left click
            if (draggingElement != null) {
                draggingElement.onMouseReleased();
                draggingElement = null;
            }
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        if (draggingElement != null) {
            draggingElement.onMouseDragged(click.x(), click.y());
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        mouseXGlobal = mouseX; // to the right of the cursor
        mouseYGlobal = mouseY;

        boolean isHoveringSomething = false;
        for (HudElement e : HudElementManager.getHudElements()) {
            if (e.isHovering(mouseX, mouseY)) {
                desc = e.name();
                isHoveringSomething = true;
            }
        }
        if (!isHoveringSomething) {
            desc = "";
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (HudElement e : HudElementManager.getHudElements()) {
            if (e.isHovering(mouseX, mouseY)) {
                float currentScale = e.scale();
                float newScale = (float) (currentScale + verticalAmount / 5);
                if (newScale > 0.3) {
                    e.setScale(newScale);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);

        for (HudElement e : HudElementManager.getHudElements()) {
            e.render(drawContext, true);
        }

        if (!desc.isEmpty()) {
            drawContext.drawText(MinecraftClient.getInstance().textRenderer, desc, (int) mouseXGlobal + 7, (int) mouseYGlobal, Color.white.getRGB(), true);
        }
    }

}
