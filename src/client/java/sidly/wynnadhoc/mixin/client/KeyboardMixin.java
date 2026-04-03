package sidly.wynnadhoc.mixin.client;

import io.github.notenoughupdates.moulconfig.platform.MoulConfigScreenComponent;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
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
        Screen currentScreen = MinecraftClient.getInstance().currentScreen;
        if (currentScreen instanceof GameOptionsScreen) return;
        if (currentScreen instanceof OptionsScreen) return;
        if (currentScreen instanceof ChatScreen) return;
        if (currentScreen instanceof MoulConfigScreenComponent) return;

        int key = input.key();
        if (MinecraftClient.getInstance().player == null) return;
        if (key == GLFW.GLFW_KEY_UNKNOWN) return;
        new KeyboardEvent(key, action);
    }
}
