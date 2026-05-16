package sidly.wynnadhoc.wapi.item;

import java.util.Set;

public record DroppedBy(String name, Set<Coords> coords) {
}
