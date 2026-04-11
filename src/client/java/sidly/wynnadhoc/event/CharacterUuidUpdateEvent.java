package sidly.wynnadhoc.event;

public class CharacterUuidUpdateEvent extends Event<CharacterUuidUpdateEvent> {
    public String uuid;

    public CharacterUuidUpdateEvent(String uuid) {
        this.uuid = uuid;
        this.fire();
    }
}
