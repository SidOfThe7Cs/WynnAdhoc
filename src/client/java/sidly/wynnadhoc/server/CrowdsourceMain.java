package sidly.wynnadhoc.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.event.CommandRegistrationEvent;
import sidly.wynnadhoc.utils.ChatMessageUtils;
import sidly.wynnadhoc.utils.Debug;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class CrowdsourceMain {
    //public static final String SERVER_URL = "http://localhost:8080";
    public static final String SERVER_URL = "https://sidly.withsidequest.com";
    public static final int TIMEOUT = 30;
    private static String state = "";

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(CrowdsourceMain.TIMEOUT))
            .build();

    public static void startAuth() {
        MinecraftClient client = MinecraftClient.getInstance();

        String url = CrowdsourceMain.SERVER_URL + "/api/auth/start";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(CrowdsourceMain.TIMEOUT))
                .header("X-Session-Token", ConfigManager.INSTANCE.getToken())
                .header("Accept", "application/json")
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .whenComplete((response, ex) -> {
                    if (ex != null) {
                        WynnAdhocClient.LOGGER.info(Debug.Type.SERVER,
                                "Failed to authenticate user: " + ex.getMessage());
                        client.execute(() -> ChatMessageUtils.sendChatMessage("§cWynnAdhoc Login failed: Server connection error"));
                        return;
                    }

                    int statusCode = response.statusCode();
                    String responseBody = response.body();

                    if (statusCode == 200) {
                        try {
                            // Parse JSON response
                            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

                            if (json.has("already_authed")) {
                                WynnAdhocClient.LOGGER.info(Debug.Type.SERVER, "refreshed session");
                                return;
                            }

                            String authUrl = json.get("authUrl").getAsString();
                            state = json.get("state").getAsString();

                            WynnAdhocClient.LOGGER.info(Debug.Type.SERVER, "Auth URL received. Opening browser...");

                            // Open the browser on the main thread
                            client.execute(() -> {
                                try {
                                    Util.getOperatingSystem().open(new URI(authUrl));
                                    ChatMessageUtils.sendChatMessage("§aBrowser opened");

                                    // Start polling for session
                                    startPollingForSession();

                                } catch (Exception e) {
                                    WynnAdhocClient.LOGGER.info(Debug.Type.SERVER, "Failed to open browser: " + e.getMessage());
                                    ChatMessageUtils.sendChatMessage("§cFailed to open browser. Please use: " + authUrl);
                                    startPollingForSession();
                                }
                            });

                        } catch (Exception e) {
                            WynnAdhocClient.LOGGER.info(Debug.Type.SERVER,
                                    "Failed to parse auth response: " + e.getMessage());
                            client.execute(() -> ChatMessageUtils.sendChatMessage("§cWynnAdhoc Login failed: Invalid server response"));
                        }

                    } else {
                        // Handle error response
                        try {
                            JsonObject error = JsonParser.parseString(responseBody).getAsJsonObject();
                            String errorMsg = error.has("message") ?
                                    error.get("message").getAsString() :
                                    "Unknown error";
                            WynnAdhocClient.LOGGER.info(Debug.Type.SERVER, "Auth failed (" + statusCode + "): " + errorMsg);
                            client.execute(() -> ChatMessageUtils.sendChatMessage("§cWynnAdhoc Login failed: " + errorMsg));
                        } catch (Exception e) {
                            WynnAdhocClient.LOGGER.info(Debug.Type.SERVER, "Auth failed with status: " + statusCode);
                            client.execute(() -> ChatMessageUtils.sendChatMessage("§cWynnAdhoc Login failed (Status: " + statusCode + ")"));
                        }
                    }
                });
    }

    private static void startPollingForSession() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        WynnAdhocClient.LOGGER.info(Debug.Type.SERVER, "Starting session polling...");
        ChatMessageUtils.sendChatMessage("§eWaiting for authorization... (check browser)");

        // Use a scheduled executor for polling
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // Poll every 2 seconds for up to 5 minutes
        scheduler.scheduleAtFixedRate(() -> {
            try {
                String verifyUrl = CrowdsourceMain.SERVER_URL + "/api/auth/verify";

                HttpRequest verifyRequest = HttpRequest.newBuilder()
                        .uri(URI.create(verifyUrl))
                        .timeout(Duration.ofSeconds(CrowdsourceMain.TIMEOUT))
                        .header("Accept", "application/json")
                        .header("Verification-State", state)
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                httpClient.sendAsync(verifyRequest, HttpResponse.BodyHandlers.ofString())
                        .whenComplete((response, ex) -> {
                            if (ex != null) {
                                WynnAdhocClient.LOGGER.info(Debug.Type.SERVER, "Waiting to be authenticated: " + ex.getMessage() + response.body());
                                return;
                            }

                            if (response.statusCode() == 200) {
                                try {
                                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

                                    // Check if we have a valid session
                                    if (json.has("token")) {
                                        storeSessionToken(json.get("token").getAsString());
                                        onAuthSuccess();
                                        scheduler.shutdownNow();
                                    }
                                } catch (Exception e) {
                                    WynnAdhocClient.LOGGER.info(Debug.Type.SERVER, "Waiting to be authenticated: " + e.getMessage() + response.body());
                                }
                            } else if (response.statusCode() == 401) {
                                WynnAdhocClient.LOGGER.info(Debug.Type.SERVER, "Waiting to be authenticated: " + response.body());
                            } else {
                                // Other error - log but continue
                                WynnAdhocClient.LOGGER.info(Debug.Type.SERVER,
                                        "Poll verify status: " + response.statusCode() + response.body());
                            }
                        });

            } catch (Exception e) {
                // Error in polling - continue
            }
        }, 5, 2, TimeUnit.SECONDS);

        // Stop polling after 5 minutes (timeout)
        scheduler.schedule(() -> {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
                client.execute(() -> ChatMessageUtils.sendChatMessage("§cWynnAdhoc login timed out, please try again. (/wynnadhoc reAuth)"));
                WynnAdhocClient.LOGGER.info(Debug.Type.SERVER, "Auth polling timed out.");
            }
        }, 2, TimeUnit.MINUTES);
    }

    private static void onAuthSuccess() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        WynnAdhocClient.LOGGER.info(Debug.Type.SERVER, "WynnAdhoc auth successful");

        client.execute(() -> ChatMessageUtils.sendChatMessage("WynnAdhoc authentication successful"));
        ConfigManager.INSTANCE.saveToken();
    }

    private static void storeSessionToken(String sessionToken) {
        ConfigManager.INSTANCE.storeToken(sessionToken);
    }

    public static void registerCommands(CommandRegistrationEvent event) {
        event.register(ClientCommandManager.literal("reAuth")
                .executes(ctx -> {
                    startAuth();
                    return 1;
                })
        );
    }

}
