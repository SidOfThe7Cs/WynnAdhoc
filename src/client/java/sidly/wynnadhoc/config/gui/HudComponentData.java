package sidly.wynnadhoc.config.gui;

import com.google.gson.annotations.Expose;
import sidly.wynnadhoc.WynnAdhocClient;

public class HudComponentData {
    @Expose
    protected final String name;
    @Expose
    protected float x;
    @Expose
    protected float y;
    @Expose
    protected float scale;
    @Expose
    protected float width;
    @Expose
    protected float height;
    @Expose
    protected Integer background;

    public HudComponentData(Builder builder) {
        this.name = builder.name;
        this.x = builder.x;
        this.y = builder.y;
        this.scale = builder.scale;
        this.width = builder.width;
        this.height = builder.height;
        this.background = builder.background;
    }

    public static class Builder {

        // 0-1, % of screen
        private final float x;
        private final float y;
        private float width = 0;
        private float height = 0;

        private final String name;
        private float scale = 1;
        private Integer background = null;

        public Builder(String name, float x, float y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }

        public Builder width(float width) {
            this.width = width;
            return this;
        }

        public Builder height(float height) {
            this.height = height;
            return this;
        }

        public Builder scale(float scale) {
            this.scale = scale;
            return this;
        }

        public Builder background(Integer background) {
            this.background = background;
            return this;
        }

        public HudComponentData build() {
            return new HudComponentData(this);
        }
    }

    public void updateDisplay() {
        HudComponent hudComponent = HudElementManager.getHudElement(this);
        if (hudComponent != null) hudComponent.updateDisplay();
        else WynnAdhocClient.LOGGER.error("Hud element with name not found");
    }

    @Override
    public String toString() {
        return "HudElementData{" +
                ", x=" + x +
                ", y=" + y +
                ", scale=" + scale +
                '}';
    }
}
