package sidly.wynnadhoc.features.lootruns;

public class BeaconEffect {
    private final int base;
    private final int change;

    public BeaconEffect(int base, int change) {
        this.base = base;
        this.change = change;
    }

    public int getBeaconEffect(boolean isVibrant, int aquaMultiplier) {
        int totalMulti = 0;
        if (isVibrant) totalMulti++;
        totalMulti += aquaMultiplier;
        totalMulti = Math.max(totalMulti, 3);
        return base + (change * totalMulti);
    }
}
