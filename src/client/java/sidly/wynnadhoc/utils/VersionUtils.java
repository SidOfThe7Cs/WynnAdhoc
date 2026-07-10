package sidly.wynnadhoc.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import kotlin.Pair;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.event.PlayerLoadedEvent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtils {

    public static final Pattern VERSION_PATTERN = Pattern.compile("^v?(\\d+)\\.(\\d+)\\.(\\d+)$");
    private static final List<Pair<String, String>> changelog = new ArrayList<>();

    static {
        changelog("0.0.0",
                """
                        """
        );
        changelog("0.0.1",
                """
                        Added in-Game Changelogs
                        Implemented Wynn Oauth2
                        Added /wynnadhoc reParty save/invite
                        Fixed not detecting left clicks on chests
                        Fixed multiple chat hover events (kinda)
                        Allowed changing chest highlight color
                        """
        );
        changelog("0.0.2",
                "Added automatic update checking"
        );
        changelog("0.0.3",
                """
                        "Added a button to the wynntills map to select chest waypoints"
                        "Added /wynnadhoc and /wynnadhoc Config as alternate ways to open the config"
                        """
        );
    }

    private static void changelog(String v, String changes) {
        changelog.add(
                new Pair<>(v, changes)
        );
    }

    public static void onPLayerLoad(PlayerLoadedEvent event) {
        Executors.newScheduledThreadPool(1).schedule(() -> {
            checkForChangelog();
            checkForNewVersion();
        }, 2, TimeUnit.SECONDS);
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
            if (other == null) return 0;
            if (this.major != other.major) return Integer.compare(this.major, other.major);
            if (this.minor != other.minor) return Integer.compare(this.minor, other.minor);
            return Integer.compare(this.hotfix, other.hotfix);
        }

        @Override
        public String toString() {
            return major + "." + minor + "." + hotfix;
        }
    }

    private static void checkForChangelog() {
        SVersion lastKnownV = SVersion.parse(ConfigManager.INSTANCE.getLastVersion());
        SVersion currentV = SVersion.parse(getCurrentVersion());
        if (lastKnownV == null || currentV == null) return;

        if (Objects.equals(lastKnownV.toString(), "0.0.0")) {
            ConfigManager.INSTANCE.setLastVersion(currentV.toString());
            return;
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

    private static void checkForNewVersion() {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/SidOfThe7Cs/WynnAdhoc/releases/latest"))
                .header("Accept", "application/vnd.github.v3+json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonElement body = JsonParser.parseString(response.body());
            String versionString = body.getAsJsonObject().get("tag_name").getAsString();
            SVersion version = SVersion.parse(versionString);
            if (version == null) return;
            if (version.compareTo(SVersion.parse(getCurrentVersion())) > 0) {
                MutableText message = Text.literal("there is is a new WynnAdhoc update available, download from ");
                MutableText link = Text.literal("https://github.com/SidOfThe7Cs/WynnAdhoc/releases/latest").styled(s ->
                        s.withColor(Formatting.BLUE)
                                .withUnderline(true)
                                .withClickEvent(new ClickEvent.OpenUrl(URI.create("https://github.com/SidOfThe7Cs/WynnAdhoc/releases/latest")))
                );
                message.getSiblings().add(link);
                ChatMessageUtils.sendChatMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
