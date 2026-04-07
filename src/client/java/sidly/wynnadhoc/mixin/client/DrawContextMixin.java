package sidly.wynnadhoc.mixin.client;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.event.DrawTooltipEvent;

import java.util.List;
import java.util.Optional;

@Mixin(DrawContext.class)
public class DrawContextMixin {
    @Inject(
            method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/util/Identifier;)V",
            at = @At(
                    value = "HEAD"
            )
    )
    private void drawTooltip(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y, @Nullable Identifier texture, CallbackInfo ci) {
        DrawTooltipEvent event = new DrawTooltipEvent(text);
        //return event.getModifiedTooltip();
    }
}
