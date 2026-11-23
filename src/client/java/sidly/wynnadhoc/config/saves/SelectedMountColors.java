package sidly.wynnadhoc.config.saves;

import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.features.mounts.MountCore;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class SelectedMountColors extends BasicSavable<SelectedMountColors> {
    public static final File SAVE_FILE = ConfigManager.getConfigDir().resolve("selected-mount-colors.json").toFile();
    private Set<MountCore> mounts = new HashSet<>();

    public Set<MountCore> getMounts() {
        return mounts;
    }

    public SelectedMountColors() {
        super(SAVE_FILE, SelectedMountColors.class);
    }

    @Override
    protected void overwrite(SelectedMountColors newInstance) {
        this.mounts = newInstance.mounts;
    }

    @Override
    protected SelectedMountColors getData() {
        return this;
    }
}
