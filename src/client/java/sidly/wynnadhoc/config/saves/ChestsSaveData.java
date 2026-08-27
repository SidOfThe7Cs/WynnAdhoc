package sidly.wynnadhoc.config.saves;

import net.minecraft.util.math.BlockPos;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.ChestConfig;
import sidly.wynnadhoc.features.chests.ChestTracker;
import sidly.wynnadhoc.models.Character;

import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ChestsSaveData extends BasicSavable<ChestsSaveData> {
    public static final File SAVE_FILE = ConfigManager.getConfigDir().resolve("chests.json").toFile();
    public Map<BlockPos, ChestData> chests = new HashMap<>();

    public ChestsSaveData() {
        super(SAVE_FILE, ChestsSaveData.class);
    }

    private static ChestConfig config() {
        return ConfigManager.INSTANCE.config.chest;
    }

    public static class ChestData {
        public final int tier;
        public final Map<String, Long> lastOpened;
        public byte[] items;

        public ChestData(int tier, byte[] items) {
            this.tier = tier;
            this.items = items;
            this.lastOpened = new HashMap<>();
        }

        public byte[] addItems(byte[] items) {
            if (this.items == null || this.items.length == 0) {
                this.items = items;
                return this.items;
            }
            byte[] newTotal = new byte[this.items.length + items.length];
            System.arraycopy(this.items, 0, newTotal, 0, this.items.length);
            System.arraycopy(items, 0, newTotal, this.items.length, items.length);
            this.items = newTotal;
            return this.items;
        }

        public void onOpen() {
            String uuid = Character.INSTANCE.uuid();
            if (uuid.isEmpty()) {
                WynnAdhocClient.LOGGER.warn("opened a chest while uuid is null last opened not saved");
                return;
            }
            this.lastOpened.put(uuid, System.currentTimeMillis());
        }

        public boolean isOpenable(Long now) {
            String uuid = Character.INSTANCE.uuid();
            if (uuid.isEmpty()) {
                return false;
            }
            return this.lastOpened.getOrDefault(uuid, -1L) + 1800000 < now;
        }

        public Color getColor(Long now) {
            String uuid = Character.INSTANCE.uuid();
            if (uuid.isEmpty()) {
                WynnAdhocClient.LOGGER.warn("failed to get chest color, uuid is null");
                return ChestTracker.INSTANCE.getColor(config().cdColor);
            }
            var color = config().cdColor;
            Long last = this.lastOpened.getOrDefault(uuid, -1L);
            // 30 minutes has passed
            if (last + 1800000 < now) color = config().openableColor;
            // never been opened or 3 days have passed
            if (last == -1L || last + TimeUnit.DAYS.toMillis(3) < now) color = config().readyColor;
            return ChestTracker.INSTANCE.getColor(color);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            ChestData chestData = (ChestData) o;
            return tier == chestData.tier && Objects.equals(lastOpened, chestData.lastOpened) && Objects.deepEquals(items, chestData.items);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tier, lastOpened, Arrays.hashCode(items));
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
