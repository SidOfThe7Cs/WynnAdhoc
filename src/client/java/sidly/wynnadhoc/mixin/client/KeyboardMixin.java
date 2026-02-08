package sidly.wynnadhoc.mixin.client;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sidly.wynnadhoc.event.KeyboardEvent;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"))
    private void onKey(long window, int action, KeyInput input, CallbackInfo ci) {
        int key = input.key();
        if (MinecraftClient.getInstance().player == null) return;
        if (key == GLFW.GLFW_KEY_UNKNOWN) return;
        new KeyboardEvent(key, action);
    }
}
