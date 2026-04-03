package sidly.wynnadhoc.event;

public class KeyboardEvent extends Event<KeyboardEvent> { // TODO consumabl
    public int key;
    public int action;

    public KeyboardEvent(int key, int action) {
        this.key = key;
        this.action = action;
        this.fire();
    }
}
