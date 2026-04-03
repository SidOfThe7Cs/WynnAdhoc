package sidly.wynnadhoc.utils.datatypes;

// Helper record for normalized coordinates
public record NormalizedBox(float x, float y, float width, float height) {

    public NormalizedBox(float x, float y, float width, float height) {
        this.x = clamp(x);
        this.y = clamp(y);
        this.width = clamp(width);
        this.height = clamp(height);
    }

    private float clamp(float v) {
        return Math.clamp(v, 0, 1);
    }
}
