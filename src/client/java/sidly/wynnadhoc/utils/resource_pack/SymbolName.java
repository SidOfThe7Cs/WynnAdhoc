package sidly.wynnadhoc.utils.resource_pack;

public enum SymbolName {
    SPACE,
    A,
    B,
    C,
    D,
    E,
    F,
    G,
    H,
    I,
    J,
    K,
    L,
    M,
    N,
    O,
    P,
    Q,
    R,
    S,
    T,
    U,
    V,
    W,
    X,
    Y,
    Z,
    GATHERING_AXE,
    GATHERING_SCYTHE,
    GATHERING_PICK,
    GATHERING_ROD,
    RING,
    BRACELET,
    NECKLACE,
    HELMET,
    CHESTPLATE,
    PANTS,
    BOOTS,
    BOW,
    WAND,
    DAGGER,
    RELIK,
    SPEAR,
    REROLL,
    FRAME,
    ZERO,
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TOOLTIP_PAGE,
    OFFHAND_KEY,
    ITEM_ROLL,
    DIVIDER,
    MAJOR_ID,
    STR_REQ_NONE,
    STR_REQ,
    DEX_REQ_NONE,
    DEX_REQ,
    INT_REQ_NONE,
    INT_REQ,
    DEF_REQ_NONE,
    DEF_REQ,
    AGI_REQ_NONE,
    AGI_REQ,
    EM,
    EB,
    LE,
    LOCKED,
    ATTK_SPEED,
    NEUTRAL,
    STR,
    DEX,
    INT,
    DEF,
    AGI,
    REQ_NONE,
    REQ_MET,
    REQ_NOT_MET;

    private final String displayName;

    SymbolName() {
        this.displayName = "(" + name() + ")";
    }

    SymbolName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public FontData getData() {
        return FontData.get(this);
    }
}
