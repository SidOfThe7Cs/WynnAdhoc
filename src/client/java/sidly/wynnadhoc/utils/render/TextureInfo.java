package sidly.wynnadhoc.utils.render;

import com.wynntils.utils.render.RenderUtils;
import com.wynntils.utils.render.Texture;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public record TextureInfo(Texture wynntillsTexture, int leftBorder, int rightBorder, int topBorder, int bottomBorder) {
    public int width() {
        return wynntillsTexture.width();
    }

    public int height() {
        return wynntillsTexture.height();
    }

    public int getDrawableWidth() {
        return width() - (leftBorder + rightBorder);
    }

    public int getDrawableHeight() {
        return height() - (topBorder + bottomBorder);
    }

    public int getX(Screen screen) {
        return (int) ((screen.width - width()) / 2f);
    }

    public int getY(Screen screen) {
        return (int) ((screen.height - height()) / 2f);
    }

    public int getDrawableX(Screen screen) {
        return getX(screen) + leftBorder;
    }

    public int getDrawableY(Screen screen) {
        return getY(screen) + topBorder;
    }

    public void enableScissor(DrawContext context, Screen s) {
        RenderUtils.enableScissor(context, getDrawableX(s), getDrawableY(s), getDrawableWidth(), getDrawableHeight());
    }

    public void disableScissor(DrawContext context) {
        RenderUtils.disableScissor(context);
    }

    public void draw(DrawContext context, Screen s) {
        RenderUtils.drawTexturedRect(context, wynntillsTexture, getX(s), getY(s));
    }
}
