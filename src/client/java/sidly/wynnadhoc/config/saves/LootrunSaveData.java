package sidly.wynnadhoc.config.saves;

import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.features.lootruns.LootrunData;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LootrunSaveData extends BasicSavable<LootrunSaveData> {
    public static final File SAVE_FILE = ConfigManager.getConfigDir().resolve("active_lootruns.json").toFile();
    public Map<String, LootrunData> lootruns = new HashMap<>();

    public LootrunSaveData() {
        super(SAVE_FILE, LootrunSaveData.class);
    }

    @Override
    protected void overwrite(LootrunSaveData newInstance) {
        this.lootruns = newInstance.lootruns;
    }

    @Override
    protected LootrunSaveData getData() {
        return this;
    }
}
