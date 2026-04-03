package sidly.wynnadhoc.mixin.client;

import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.event.MouseButtonEvent;
import sidly.wynnadhoc.event.MouseMoveEvent;
import sidly.wynnadhoc.event.MouseScrollEvent;

@Mixin(Mouse.class)
public class MouseMixin {

    @Shadow
    private MouseInput activeButton;
    @Shadow
    private double x;
    @Shadow
    private double y;

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, MouseInput input, int action, CallbackInfo ci) {
        MouseButtonEvent event = new MouseButtonEvent(input, action);
        if (event.consumed()) ci.cancel();
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"))
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        new MouseScrollEvent(vertical);
    }

    @Inject(method = "onCursorPos", at = @At("HEAD"))
    private void onCursorPos(long window, double cursorX, double cursorY, CallbackInfo ci) {
        Vector2d oldPos = new Vector2d(this.x, this.y);
        Vector2d newPos = new Vector2d(cursorX, cursorY);
        new MouseMoveEvent(oldPos, newPos, activeButton);
    }
}
