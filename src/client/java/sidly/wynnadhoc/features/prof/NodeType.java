package sidly.wynnadhoc.features.prof;

public enum NodeType {
    Redwood,
    Cinnabar,
    UNKNOWN;

    public static NodeType fromStr(String s) {
        try {
            return NodeType.valueOf(s);
        } catch (Exception e) {
            return UNKNOWN;
        }
    }
}
