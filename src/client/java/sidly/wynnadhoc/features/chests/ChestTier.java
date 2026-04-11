package sidly.wynnadhoc.features.chests;


public enum ChestTier {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    UNKNOWN(-1);

    private final Integer num;

    public Integer asInt() {
        return num;
    }

    public static ChestTier from(Integer num) {
        switch (num) {
            case 1 -> {
                return ONE;
            }
            case 2 -> {
                return TWO;
            }
            case 3 -> {
                return THREE;
            }
            case 4 -> {
                return FOUR;
            }
            default -> {
                return UNKNOWN;
            }
        }
    }

    ChestTier(Integer num) {
        this.num = num;
    }
}
