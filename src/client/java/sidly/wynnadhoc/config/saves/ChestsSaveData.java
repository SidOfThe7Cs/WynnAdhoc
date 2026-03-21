package sidly.wynnadhoc.config.saves;

import net.minecraft.util.math.BlockPos;
import sidly.wynnadhoc.config.ConfigManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ChestsSaveData extends BasicSavable<ChestsSaveData> {
    public static final File SAVE_FILE = ConfigManager.getConfigDir().resolve("chests.json").toFile();
    public Map<BlockPos, Long> chests = new HashMap<>();

    public ChestsSaveData() {
        super(SAVE_FILE, ChestsSaveData.class);
    }

    @Override
    protected void overwrite(ChestsSaveData newInstance) {
        this.chests = newInstance.chests;
    }

    @Override
    protected ChestsSaveData getData() {
        return this;
    }
}
