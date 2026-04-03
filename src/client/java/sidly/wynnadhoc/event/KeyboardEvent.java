package sidly.wynnadhoc.event;

import net.minecraft.client.input.KeyInput;

public class KeyboardEvent extends Event<KeyboardEvent> {
    public int action;
    public KeyInput keyInput;

    public KeyboardEvent(KeyInput key, int action) {
        this.keyInput = key;
        this.action = action;
        this.fire();
    }
}
