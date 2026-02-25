package sidly.wynnadhoc.utils.datatypes;

public record LevelRange(int min, int max) {
    @Override
    public String toString() {
        return min + " - " + max;
    }
}
