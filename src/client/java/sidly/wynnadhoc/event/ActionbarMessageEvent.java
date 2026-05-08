package sidly.wynnadhoc.event;

import net.minecraft.text.Text;

public class ActionbarMessageEvent extends Event<ActionbarMessageEvent> {
    public Text original;

    public ActionbarMessageEvent(Text original) {
        this.original = original;
        this.fire();
        //WynnAdhocClient.LOGGER.info(Debug.Type.TEMP, "actione bar: " + FontUtils.translate(original));
    }
}
