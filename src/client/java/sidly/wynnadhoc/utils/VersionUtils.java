package sidly.wynnadhoc.utils;

import kotlin.Pair;
import net.fabricmc.loader.api.FabricLoader;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.event.PlayerLoadedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtils {

    public static final Pattern VERSION_PATTERN = Pattern.compile("^v?(\\d+)\\.(\\d+)\\.(\\d+)$");
    private static final List<Pair<String, String>> changelog = new ArrayList<>();

    static {
        changelog("0.0.0", "V0 shouldn't ever print changelog");
        changelog("0.0.1",
                """
                        Added in-Game Changelogs
                        Implemented Wynn Oauth2
                        Added /wynnadhoc reParty save/invite
                        """
        );
        changelog("0.0.2",
                "This is a future version"
        );
    }

    private static void changelog(String v, String changes) {
        changelog.add(
                new Pair<>(v, changes)
        );
    }

    public static void onPLayerLoad(PlayerLoadedEvent event) {
        SVersion lastKnownV = SVersion.parse(ConfigManager.INSTANCE.getLastVersion());
        SVersion currentV = SVersion.parse(getCurrentVersion());
        if (lastKnownV == null || currentV == null) return;

        if (Objects.equals(lastKnownV.toString(), "0.0.0")) {
            // TODO return
        }

        StringBuilder sb = new StringBuilder();

        for (Pair<String, String> entry : changelog) {
            SVersion changesV = SVersion.parse(entry.getFirst());
            if (changesV != null && changesV.isBetween(lastKnownV, currentV)) {
                sb.append(entry.getFirst()).append("\n").append(entry.getSecond());
            }
        }

        if (!sb.isEmpty()) {
            ChatMessageUtils.sendChatMessage("WynnAdhoc Changelog:\n" + sb);
        }

        ConfigManager.INSTANCE.setLastVersion(currentV.toString());
    }

    public static String getCurrentVersion() {
        return FabricLoader.getInstance()
                .getModContainer(WynnAdhocClient.MOD_ID)
                .orElseThrow()
                .getMetadata()
                .getVersion()
                .getFriendlyString();
    }

    public static class SVersion {
        public final int major;
        public final int minor;
        public final int hotfix;

        private SVersion(int major, int minor, int hotfix) {
            this.major = major;
            this.minor = minor;
            this.hotfix = hotfix;
        }

        public static SVersion parse(String version) {
            Matcher matcher = VERSION_PATTERN.matcher(version);
            if (!matcher.matches()) {
                return null;
            }
            return new SVersion(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3))
            );
        }

        public boolean isBetween(SVersion min, SVersion max) {
            return compareTo(min) > 0 && compareTo(max) <= 0;
        }

        public int compareTo(SVersion other) {
            if (this.major != other.major) return Integer.compare(this.major, other.major);
            if (this.minor != other.minor) return Integer.compare(this.minor, other.minor);
            return Integer.compare(this.hotfix, other.hotfix);
        }

        @Override
        public String toString() {
            return major + "." + minor + "." + hotfix;
        }
    }
}
