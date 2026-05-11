package sidly.wynnadhoc.config.saves;

import com.wynntils.core.components.Models;
import net.minecraft.util.math.BlockPos;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.ConfigManager;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ChestsSaveData extends BasicSavable<ChestsSaveData> {
    public static final File SAVE_FILE = ConfigManager.getConfigDir().resolve("chests.json").toFile();
    public Map<BlockPos, ChestData> chests = new HashMap<>();

    public ChestsSaveData() {
        super(SAVE_FILE, ChestsSaveData.class);
    }

    public static class ChestData {
        public int tier;
        public Map<String, Long> lastOpened;

        public ChestData(int tier) {
            this.tier = tier;
            this.lastOpened = new HashMap<>();
        }

        public void onOpen() {
            String uuid = Models.Character.getId();
            if (uuid.isEmpty()) {
                WynnAdhocClient.LOGGER.warn("opened a chest while uuid is null last opened not saved");
                return;
            }
            this.lastOpened.put(uuid, System.currentTimeMillis());
        }

        public Color getColor(Long now) {
            String uuid = Models.Character.getId();
            if (uuid.isEmpty()) {
                WynnAdhocClient.LOGGER.warn("failed to get chest color, uuid is null");
                return Color.RED;
            }
            var color = Color.RED;
            Long last = this.lastOpened.getOrDefault(uuid, -1L);
            // 30 minutes has passed
            if (last + 1800000 < now) color = Color.YELLOW;
            // never been opened or 3 days have passed
            if (last == -1L || last + TimeUnit.DAYS.toMillis(3) < now) color = Color.GREEN;
            return color;
        }
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
