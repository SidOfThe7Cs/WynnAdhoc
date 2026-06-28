package sidly.wynnadhoc.config.saves;

import sidly.wynnadhoc.config.ConfigManager;

import java.io.File;

public class LastVersion extends BasicSavable<LastVersion> {
    public static final File SAVE_FILE = ConfigManager.getConfigDir().resolve("last_version.json").toFile();
    public String lastVersion;

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
