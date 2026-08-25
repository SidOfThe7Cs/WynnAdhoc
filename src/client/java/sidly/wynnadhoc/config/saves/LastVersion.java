package sidly.wynnadhoc.config.saves;

import sidly.wynnadhoc.config.ConfigManager;

import java.io.File;

public class LastVersion extends BasicSavable<LastVersion> {
    public static final File SAVE_FILE = ConfigManager.getConfigDir().resolve("last_version.json").toFile();
    private String lastVersion;

    public String getLastVersion() {
        return lastVersion == null ? "0.0.0" : lastVersion;
    }

    public void setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
        changed();
    }

    public LastVersion() {
        super(SAVE_FILE, LastVersion.class);
    }

    @Override
    protected void overwrite(LastVersion newInstance) {
        this.lastVersion = newInstance.lastVersion;
    }

    @Override
    protected LastVersion getData() {
        return this;
    }
}
