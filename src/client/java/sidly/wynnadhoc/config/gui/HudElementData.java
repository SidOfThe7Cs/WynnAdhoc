package sidly.wynnadhoc.config.gui;

import com.google.gson.annotations.Expose;

public class HudElementData {
    @Expose
    protected String name;
    @Expose
    protected int x;
    @Expose
    protected int y;
    @Expose
    protected float scale;

    public HudElementData(String name, int x, int y, float scale) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.scale = scale;
    }
}
