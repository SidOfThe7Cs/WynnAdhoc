package sidly.wynnadhoc.config.saves;

import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.ConfigUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static sidly.wynnadhoc.config.ConfigManager.GSON;

public abstract class BasicSavable<T> {
    private static final Set<BasicSavable<?>> all = new HashSet<>();
    public static void loadAll() {
        Set<BasicSavable<?>> snapshot = new HashSet<>(all);
        for (BasicSavable<?> basicSavable : snapshot) {
            basicSavable.load();
        }
    }

    public static void saveAll() {
        Set<BasicSavable<?>> snapshot = new HashSet<>(all);
        for (BasicSavable<?> basicSavable : snapshot) {
            basicSavable.save();
        }
    }


    private transient final File SAVE_FILE;
    private transient final Class<T> CLASS;

    public BasicSavable(File saveFile, Class<T> CLASS) {
        this.SAVE_FILE = saveFile;
        this.CLASS = CLASS;
        all.add(this);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void load() {
        SAVE_FILE.getParentFile().mkdirs();
        try {
            SAVE_FILE.createNewFile();
        } catch (IOException e) {
            WynnAdhocClient.LOGGER.error("Could not create config file!" + e);
        }
        T loaded = ConfigUtil.loadConfig(CLASS, SAVE_FILE, GSON);

        if (loaded != null) {
            overwrite(loaded);
        } else {
            WynnAdhocClient.LOGGER.warn("Config file could not be loaded");
        }
    }

    public void save() {
        ConfigUtil.saveConfig(getData(), SAVE_FILE, GSON);
    }

    protected abstract void overwrite(T newInstance);

    protected abstract T getData();
}
