package sidly.wynnadhoc.config.saves;

import sidly.wynnadhoc.config.ConfigManager;

import java.io.File;

public class SessionToken extends BasicSavable<SessionToken> {
    public static final File SAVE_FILE = ConfigManager.getConfigDir().resolve("session_token.json").toFile();
    public String token;

    public SessionToken() {
        super(SAVE_FILE, SessionToken.class);
    }

    @Override
    protected void overwrite(SessionToken newInstance) {
        this.token = newInstance.token;
    }

    @Override
    protected SessionToken getData() {
        return this;
    }
}
