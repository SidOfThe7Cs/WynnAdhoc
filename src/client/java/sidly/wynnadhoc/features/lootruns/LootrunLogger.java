package sidly.wynnadhoc.features.lootruns;

import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.ConfigUtil;
import sidly.wynnadhoc.config.saves.BasicSavable;
import sidly.wynnadhoc.utils.FormatUtils;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class LootrunLogger {
    private static final Path SAVE_DIR = ConfigManager.getConfigDir().resolve("lootrun_logs");
    private static Log INSTANCE;

    public static void load() {
        INSTANCE = new Log();
    }

    public static LootrunLogger getLogger() {
        LootrunData data = LootrunCore.INSTANCE.getCurrentLootrunData();
        if (data == null) {
            return null;
        }
        return INSTANCE.activeLogs.computeIfAbsent(data.getUuid(), k -> new LootrunLogger());
    }

    public static void endRun() {
        LootrunData data = LootrunCore.INSTANCE.getCurrentLootrunData();
        if (data == null) {
            throw  new IllegalStateException("uuid is null");
        }
        LootrunLogger logger = getLogger();
        if (logger == null) {
            WynnAdhocClient.LOGGER.warn("failed to get logger when ending lootrun the file was not saved");
            return;
        }
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); // TODO this doesnt work
        File file = SAVE_DIR.resolve("lootrun_" + data.getUuid() + "_" + time + ".txt").toFile();
        ConfigUtil.saveConfig(logger.loggedData, file);
        INSTANCE.endRun(data.getUuid());
    }

    // TODO (make types for everything with an interface) a new beacon without valid time = just relogged
    public static void appendLine(String info) {
        LootrunLogger logger = getLogger();
        if (logger == null) {
            WynnAdhocClient.LOGGER.warn("failed to get logger when appending line: " + info);
            return;
        }
        int current = ScoreboardInfo.currentMissionsCompleted;
        int max = ScoreboardInfo.currentMaxMissions;
        int timeLeft = ScoreboardInfo.timeLeft;

        StringBuilder sb = new StringBuilder();
        if (max != -1) sb.append("[").append(current).append("/").append(max).append("] ");
        if (timeLeft != -1) sb.append("[").append(FormatUtils.formatTime(timeLeft, ChronoUnit.SECONDS)).append("] ");
        sb.append(info);
        logger.loggedData.add(sb.toString());
    }

    private final List<String> loggedData = new ArrayList<>();

    public static class Log extends BasicSavable<Log> {
        public static final File SAVE_FILE = SAVE_DIR.resolve("active").toFile();
        public Map<String, LootrunLogger> activeLogs = new HashMap<>();

        public Log() {
            super(SAVE_FILE, Log.class);
        }

        protected void endRun(String uuid) {
            activeLogs.remove(uuid);
        }

        @Override
        protected void overwrite(Log newInstance) {
            this.activeLogs = newInstance.activeLogs;
        }

        @Override
        protected Log getData() {
            return this;
        }
    }
}
