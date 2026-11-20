package sidly.wynnadhoc.utils.gui;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomButton {
    protected final Text text;
    protected final List<String> aliases = new ArrayList<>();

    public CustomButton(Text text) {
        this.text = text;
    }

    public boolean matches(String s) {
        if (text.getString().contains(s)) return true;
        return aliases.stream().anyMatch(a -> a.contains(s));
    }

    public void addAlias(String s) {
        aliases.add(s);
    }

    public Text getText() {
        return text;
    }

    public abstract void onClick(Click click, boolean doubled);

    public abstract boolean isVisible();

    public abstract boolean isHovered();

    /**
     * @return the amount to be added to the yOffset
     */
    public abstract int render(DrawContext context, int mouseX, int mouseY, float partialTick, Screen parent, ButtonContainer container, int yOffset);

}
