package sidly.wynnadhoc.utils.resource_pack;

import net.minecraft.util.Identifier;
import org.joml.Vector2i;
import org.jspecify.annotations.NonNull;
import sidly.wynnadhoc.event.InitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FontData {
    private static final Map<Integer, FontData> byHash = new HashMap<>();
    private static final Map<SymbolName, FontData> byName = new HashMap<>();

    private static int getHash(Identifier id, Vector2i pos) {
        if (pos == null) return Objects.hash(id);
        return Objects.hash(id.toString(), pos.x, pos.y);
    }

    public static FontData get(Identifier id, Vector2i pos) {
        if (id == null) return null;
        FontData result = byHash.get(getHash(id, pos));
        if (result == null) result = byHash.get(getHash(id, null));
        return result;
    }

    public static FontData get(SymbolName symbolName) {
        return byName.get(symbolName);
    }

    private final Identifier id;
    private final SymbolName name;

    public FontData(@NonNull String path, @NonNull Vector2i pos, @NonNull SymbolName name) {
        this.id = Identifier.ofVanilla("textures/font/" + path);
        this.name = name;
        byHash.put(getHash(id, pos), this);
        byName.put(name, this);
    }

    public FontData(@NonNull String path, @NonNull SymbolName name) {
        this.id = Identifier.ofVanilla("textures/font/" + path);
        this.name = name;
        byHash.put(getHash(id, null), this);
        byName.put(name, this);
    }

    public Identifier getId() {
        return id;
    }

    public SymbolName getSymbolName() {
        return name;
    }

    public String getDisplayName() {
        return name.getDisplayName();
    }


    public static void init(InitEvent empty) {
        new FontData("tooltip/identification/major.png", SymbolName.MAJOR_ID);
        new FontData("tooltip/divider/line.png", SymbolName.DIVIDER);
        new FontData("tooltip/identification/meter_counter.png", SymbolName.ITEM_ROLL);
        new FontData("tile/swap.png", SymbolName.OFFHAND_KEY);
        new FontData("tooltip/page.png", SymbolName.TOOLTIP_PAGE);
        new FontData("tooltip/emblem/frame.png", SymbolName.FRAME);
        new FontData("tooltip/requirement/frame.png", SymbolName.FRAME); // could get rarity from this

        new FontData("tooltip/requirement/linear.png", new Vector2i(0, 0), SymbolName.REQ_NONE);
        new FontData("tooltip/requirement/linear.png", new Vector2i(1, 0), SymbolName.REQ_MET);
        new FontData("tooltip/requirement/linear.png", new Vector2i(2, 0), SymbolName.REQ_NOT_MET);

        new FontData("tooltip/attribute/sprite.png", new Vector2i(0, 0), SymbolName.STR);
        new FontData("tooltip/attribute/sprite.png", new Vector2i(1, 0), SymbolName.DEX);
        new FontData("tooltip/attribute/sprite.png", new Vector2i(2, 0), SymbolName.INT);
        new FontData("tooltip/attribute/sprite.png", new Vector2i(3, 0), SymbolName.DEF);
        new FontData("tooltip/attribute/sprite.png", new Vector2i(4, 0), SymbolName.AGI);
        new FontData("tooltip/attribute/sprite.png", new Vector2i(5, 0), SymbolName.NEUTRAL);
        new FontData("tooltip/attribute/sprite.png", new Vector2i(7, 0), SymbolName.ATTK_SPEED);
        new FontData("tooltip/attribute/sprite.png", new Vector2i(8, 0), SymbolName.LOCKED);
        new FontData("tooltip/attribute/sprite.png", new Vector2i(9, 0), SymbolName.EM);
        new FontData("tooltip/attribute/sprite.png", new Vector2i(10, 0), SymbolName.EB);
        new FontData("tooltip/attribute/sprite.png", new Vector2i(11, 0), SymbolName.LE);

        new FontData("tooltip/requirement/sprite.png", new Vector2i(0, 0), SymbolName.STR_REQ);
        new FontData("tooltip/requirement/sprite.png", new Vector2i(1, 0), SymbolName.DEX_REQ);
        new FontData("tooltip/requirement/sprite.png", new Vector2i(2, 0), SymbolName.INT_REQ);
        new FontData("tooltip/requirement/sprite.png", new Vector2i(3, 0), SymbolName.DEF_REQ);
        new FontData("tooltip/requirement/sprite.png", new Vector2i(4, 0), SymbolName.AGI_REQ);
        new FontData("tooltip/requirement/sprite.png", new Vector2i(0, 1), SymbolName.STR_REQ_NONE);
        new FontData("tooltip/requirement/sprite.png", new Vector2i(1, 1), SymbolName.DEX_REQ_NONE);
        new FontData("tooltip/requirement/sprite.png", new Vector2i(2, 1), SymbolName.INT_REQ_NONE);
        new FontData("tooltip/requirement/sprite.png", new Vector2i(3, 1), SymbolName.DEF_REQ_NONE);
        new FontData("tooltip/requirement/sprite.png", new Vector2i(4, 1), SymbolName.AGI_REQ_NONE);

        new FontData("offset/wynncraft_quad.png", new Vector2i(0, 1), SymbolName.ZERO);
        new FontData("offset/wynncraft_quad.png", new Vector2i(1, 1), SymbolName.ONE);
        new FontData("offset/wynncraft_quad.png", new Vector2i(2, 1), SymbolName.TWO);
        new FontData("offset/wynncraft_quad.png", new Vector2i(3, 1), SymbolName.THREE);
        new FontData("offset/wynncraft_quad.png", new Vector2i(4, 1), SymbolName.FOUR);
        new FontData("offset/wynncraft_quad.png", new Vector2i(5, 1), SymbolName.FIVE);
        new FontData("offset/wynncraft_quad.png", new Vector2i(6, 1), SymbolName.SIX);
        new FontData("offset/wynncraft_quad.png", new Vector2i(7, 1), SymbolName.SEVEN);
        new FontData("offset/wynncraft_quad.png", new Vector2i(8, 1), SymbolName.EIGHT);
        new FontData("offset/wynncraft_quad.png", new Vector2i(9, 1), SymbolName.NINE);

        new FontData("tooltip/banner.png", new Vector2i(0, 0), SymbolName.EM);
        new FontData("tooltip/banner.png", new Vector2i(1, 0), SymbolName.EM);
        new FontData("tooltip/banner.png", new Vector2i(0, 5), SymbolName.REROLL);
        new FontData("tooltip/banner.png", new Vector2i(1, 5), SymbolName.REROLL);
        new FontData("tooltip/banner.png", new Vector2i(0, 6), SymbolName.STR);
        new FontData("tooltip/banner.png", new Vector2i(1, 6), SymbolName.STR);
        new FontData("tooltip/banner.png", new Vector2i(0, 7), SymbolName.DEX);
        new FontData("tooltip/banner.png", new Vector2i(1, 7), SymbolName.DEX);
        new FontData("tooltip/banner.png", new Vector2i(0, 8), SymbolName.INT);
        new FontData("tooltip/banner.png", new Vector2i(1, 8), SymbolName.INT);
        new FontData("tooltip/banner.png", new Vector2i(0, 9), SymbolName.DEF);
        new FontData("tooltip/banner.png", new Vector2i(1, 9), SymbolName.DEF);
        new FontData("tooltip/banner.png", new Vector2i(0, 10), SymbolName.AGI);
        new FontData("tooltip/banner.png", new Vector2i(1, 10), SymbolName.AGI);

        new FontData("tooltip/emblem/sprite.png", new Vector2i(0, 0), SymbolName.HELMET);
        new FontData("tooltip/emblem/sprite.png", new Vector2i(1, 0), SymbolName.CHESTPLATE);
        new FontData("tooltip/emblem/sprite.png", new Vector2i(2, 0), SymbolName.PANTS);
        new FontData("tooltip/emblem/sprite.png", new Vector2i(3, 0), SymbolName.BOOTS);
        new FontData("tooltip/emblem/sprite.png", new Vector2i(4, 0), SymbolName.BOW);
        new FontData("tooltip/emblem/sprite.png", new Vector2i(5, 0), SymbolName.DAGGER);
        new FontData("tooltip/emblem/sprite.png", new Vector2i(6, 0), SymbolName.WAND);
        new FontData("tooltip/emblem/sprite.png", new Vector2i(7, 0), SymbolName.RELIK);
        new FontData("tooltip/emblem/sprite.png", new Vector2i(8, 0), SymbolName.SPEAR);
        new FontData("tooltip/emblem/sprite.png", new Vector2i(0, 1), SymbolName.GATHERING_AXE);
        new FontData("tooltip/emblem/sprite.png", new Vector2i(1, 1), SymbolName.GATHERING_SCYTHE);
        new FontData("tooltip/emblem/sprite.png", new Vector2i(2, 1), SymbolName.GATHERING_PICK);
        new FontData("tooltip/emblem/sprite.png", new Vector2i(3, 1), SymbolName.GATHERING_ROD);
        new FontData("tooltip/emblem/sprite.png", new Vector2i(4, 1), SymbolName.RING);
        new FontData("tooltip/emblem/sprite.png", new Vector2i(5, 1), SymbolName.BRACELET);
        new FontData("tooltip/emblem/sprite.png", new Vector2i(6, 1), SymbolName.NECKLACE);

        new FontData("banner/box.png", new Vector2i(0, 4), SymbolName.A);
        new FontData("banner/box.png", new Vector2i(1, 4), SymbolName.B);
        new FontData("banner/box.png", new Vector2i(2, 4), SymbolName.C);
        new FontData("banner/box.png", new Vector2i(3, 4), SymbolName.D);
        new FontData("banner/box.png", new Vector2i(4, 4), SymbolName.E);
        new FontData("banner/box.png", new Vector2i(5, 4), SymbolName.F);
        new FontData("banner/box.png", new Vector2i(6, 4), SymbolName.G);
        new FontData("banner/box.png", new Vector2i(7, 4), SymbolName.H);
        new FontData("banner/box.png", new Vector2i(8, 4), SymbolName.I);
        new FontData("banner/box.png", new Vector2i(9, 4), SymbolName.J);
        new FontData("banner/box.png", new Vector2i(10, 4), SymbolName.K);
        new FontData("banner/box.png", new Vector2i(11, 4), SymbolName.L);
        new FontData("banner/box.png", new Vector2i(12, 4), SymbolName.M);
        new FontData("banner/box.png", new Vector2i(13, 4), SymbolName.N);
        new FontData("banner/box.png", new Vector2i(14, 4), SymbolName.O);
        new FontData("banner/box.png", new Vector2i(15, 4), SymbolName.P);
        new FontData("banner/box.png", new Vector2i(0, 5), SymbolName.Q);
        new FontData("banner/box.png", new Vector2i(1, 5), SymbolName.R);
        new FontData("banner/box.png", new Vector2i(2, 5), SymbolName.S);
        new FontData("banner/box.png", new Vector2i(3, 5), SymbolName.T);
        new FontData("banner/box.png", new Vector2i(4, 5), SymbolName.U);
        new FontData("banner/box.png", new Vector2i(5, 5), SymbolName.V);
        new FontData("banner/box.png", new Vector2i(6, 5), SymbolName.W);
        new FontData("banner/box.png", new Vector2i(7, 5), SymbolName.X);
        new FontData("banner/box.png", new Vector2i(8, 5), SymbolName.Y);
        new FontData("banner/box.png", new Vector2i(9, 5), SymbolName.Z);
        new FontData("banner/box.png", new Vector2i(13, 7), SymbolName.SPACE);
        new FontData("banner/box.png", new Vector2i(15, 7), SymbolName.SPACE);

        new FontData("banner/box.png", new Vector2i(0, 0), SymbolName.A);
        new FontData("banner/box.png", new Vector2i(1, 0), SymbolName.B);
        new FontData("banner/box.png", new Vector2i(2, 0), SymbolName.C);
        new FontData("banner/box.png", new Vector2i(3, 0), SymbolName.D);
        new FontData("banner/box.png", new Vector2i(4, 0), SymbolName.E);
        new FontData("banner/box.png", new Vector2i(5, 0), SymbolName.F);
        new FontData("banner/box.png", new Vector2i(6, 0), SymbolName.G);
        new FontData("banner/box.png", new Vector2i(7, 0), SymbolName.H);
        new FontData("banner/box.png", new Vector2i(8, 0), SymbolName.I);
        new FontData("banner/box.png", new Vector2i(9, 0), SymbolName.J);
        new FontData("banner/box.png", new Vector2i(10, 0), SymbolName.K);
        new FontData("banner/box.png", new Vector2i(11, 0), SymbolName.L);
        new FontData("banner/box.png", new Vector2i(12, 0), SymbolName.M);
        new FontData("banner/box.png", new Vector2i(13, 0), SymbolName.N);
        new FontData("banner/box.png", new Vector2i(14, 0), SymbolName.O);
        new FontData("banner/box.png", new Vector2i(15, 0), SymbolName.P);
        new FontData("banner/box.png", new Vector2i(0, 1), SymbolName.Q);
        new FontData("banner/box.png", new Vector2i(1, 1), SymbolName.R);
        new FontData("banner/box.png", new Vector2i(2, 1), SymbolName.S);
        new FontData("banner/box.png", new Vector2i(3, 1), SymbolName.T);
        new FontData("banner/box.png", new Vector2i(4, 1), SymbolName.U);
        new FontData("banner/box.png", new Vector2i(5, 1), SymbolName.V);
        new FontData("banner/box.png", new Vector2i(6, 1), SymbolName.W);
        new FontData("banner/box.png", new Vector2i(7, 1), SymbolName.X);
        new FontData("banner/box.png", new Vector2i(8, 1), SymbolName.Y);
        new FontData("banner/box.png", new Vector2i(9, 1), SymbolName.Z);
    }
}
