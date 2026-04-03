package sidly.wynnadhoc.config.gui;

import com.google.gson.annotations.Expose;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import org.joml.Vector2i;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.utils.render.RenderUtilsKt;

public class SubViewPort extends HudComponentData {
    @Expose
    protected int width;
    @Expose
    protected int height;
    @Expose
    protected Integer background;

    public SubViewPort(String name, float x, float y, int width, int height, Integer background) {
        super(name, x, y, 1);
        this.width = width;
        this.height = height;
        this.background = background;
    }

    public int getWidth() {
        if (width <= 1) {
            return MinecraftClient.getInstance().getWindow().getScaledWidth() * width;
        }
        return width;
    }

    public int getHeight() {
        if (height <= 1) {
            return MinecraftClient.getInstance().getWindow().getScaledHeight() * height;
        }
        return height;
    }

    public Vector2i renderPos() {
        Window window = MinecraftClient.getInstance().getWindow();
        int screenWidth = window.getScaledWidth();
        int screenHeight = window.getScaledHeight();

        int x = (int) (this.x * screenWidth);
        int y = (int) (this.y * screenHeight);

        return new Vector2i(x, y);
    }

    public void render(DrawContext context) {
        if (background == null) return;
        Vector2i pos = renderPos();
        RenderUtilsKt.drawBackground(context, pos.x, pos.y, (pos.x + this.width), (pos.y + this.height), background, 3);
    }

    @Override
    public void updateDisplay() {
        WynnAdhocClient.LOGGER.warn("cannot update display for a SubViewPort");
    }
}
