package sidly.wynnadhoc.event;

public class ButtonPressEvent extends Event<ButtonPressEvent> {
    public int button;

    public ButtonPressEvent(int button) {
        this.button = button;
        this.fire();
    }

    public boolean isButton(int button) {
        return this.button == button;
    }

    public static void onMouse(MouseButtonEvent event) {
        if (event.isPress()) new ButtonPressEvent(event.input.button());
    }

    public static void onKey(KeyboardEvent event) {
        if (event.action == 1) new ButtonPressEvent(event.key);
    }
}
