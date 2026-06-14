package sidly.wynnadhoc.features.lootruns;

import sidly.wynnadhoc.features.lootruns.enums.AquaStatus;

public class BeaconEffect {
    private final int normal;
    private final int aquaNormal;
    private final int vibAquaNormal;
    private final int vib;
    private final int aquaVibNormal;
    private final int vibAquaVibNormal;

    public BeaconEffect(int normal, int aquaNormal, int vibAquaNormal, int vib, int aquaVibNormal, int vibAquaVibNormal) {
        this.normal = normal;
        this.aquaNormal = aquaNormal;
        this.vibAquaNormal = vibAquaNormal;
        this.vib = vib;
        this.aquaVibNormal = aquaVibNormal;
        this.vibAquaVibNormal = vibAquaVibNormal;
    }

    public BeaconEffect(int normal, int aquaNormal, int vibAquaNormal, int vibAquaVibNormal) {
        this.normal = normal;
        this.aquaNormal = aquaNormal;
        this.vibAquaNormal = vibAquaNormal;
        this.vib = aquaNormal;
        this.aquaVibNormal = vibAquaNormal;
        this.vibAquaVibNormal = vibAquaVibNormal;
    }

    public int getBeaconEffect(boolean isVibrant, AquaStatus isAqua) {
        if (!isVibrant) {
            return switch (isAqua) {
                case Inactive -> normal;
                case Active -> aquaNormal;
                case Vibrant -> vibAquaNormal;
            };
        } else return switch (isAqua) {
            case Inactive -> vib;
            case Active -> aquaVibNormal;
            case Vibrant -> vibAquaVibNormal;
        };
    }
}
