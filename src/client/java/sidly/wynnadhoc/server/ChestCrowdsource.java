package sidly.wynnadhoc.server;

import com.google.gson.reflect.TypeToken;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.ChestConfig;
import sidly.wynnadhoc.features.chests.LootChest;
import sidly.wynnadhoc.utils.Debug;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static sidly.wynnadhoc.config.ConfigManager.GSON;

public class ChestCrowdsource {
    private static ChestConfig config() {
        return ConfigManager.INSTANCE.config.chest;
    }

    public static final List<LootChest> newChests = new ArrayList<>();

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(CrowdsourceMain.timeout))
            .build();

    public static CompletableFuture<List<LootChest>> getChests(boolean onlyVerified) {
        String url = CrowdsourceMain.serverUrl + "/api/chests/" + (onlyVerified ? "known" : "all");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(CrowdsourceMain.timeout))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .<List<LootChest>>thenApply(response -> {
                    if (response.statusCode() == 200) {
                        Type listType = new TypeToken<ArrayList<LootChest>>() {
                        }.getType();
                        return GSON.fromJson(response.body(), listType);
                    } else {
                        System.err.println("Failed to get chests: " + response.statusCode());
                        return new ArrayList<>();
                    }
                })
                .exceptionally(ex -> {
                    System.err.println("Error getting chests: " + ex.getMessage());
                    return new ArrayList<>();
                });
    }

    public static void submitChests() {
        if (!config().syncChests) return;
        String url = CrowdsourceMain.serverUrl + "/api/chests/submit";
        int count = newChests.size();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(CrowdsourceMain.timeout))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(newChests)))
                .build();

        newChests.clear();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .whenComplete((response, ex) -> {
                    if (ex != null) {
                        WynnAdhocClient.LOGGER.info(Debug.Type.SERVER, "Failed to submit chests: " + ex.getMessage());
                    }

                    if (response.statusCode() == 200) {
                        WynnAdhocClient.LOGGER.info(Debug.Type.SERVER, "submit" + count + "chests");
                    } else {
                        WynnAdhocClient.LOGGER.info(Debug.Type.SERVER, "Failed to submit chests: " + response.statusCode());
                    }
                });
    }

    public static void submitChests(ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient minecraftClient) {
        submitChests();
    }
}
