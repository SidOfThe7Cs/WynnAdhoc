package sidly.wynnadhoc.config.gui;

import com.google.gson.annotations.Expose;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.utils.render.RenderUtilsKt;

public class SubViewPort extends HudComponentData {
    @Expose
    protected int width;
    @Expose
    protected int height;
    @Expose
    protected Integer background;

    public SubViewPort(float x, float y, int width, int height, Integer background) {
        super("ViewPort", x, y, 1);
        this.width = width;
        this.height = height;
        this.background = background;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void render(DrawContext context) {
        if (background == null) return;
        Window window = MinecraftClient.getInstance().getWindow();
        int screenWidth = window.getScaledWidth();
        int screenHeight = window.getScaledHeight();

        int x = (int) (this.x * screenWidth);
        int y = (int) (this.y * screenHeight);

        RenderUtilsKt.drawBackground(context, x, y, (x * this.width), (y * this.height), background, 3);
    }

    @Override
    public void updateDisplay() {
        WynnAdhocClient.LOGGER.warn("cannot update display for a SubViewPort");
    }
}
