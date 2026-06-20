package sidly.wynnadhoc.server;

import com.google.gson.reflect.TypeToken;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.ChestConfig;
import sidly.wynnadhoc.event.CommandRegistrationEvent;
import sidly.wynnadhoc.features.chests.LootChest;
import sidly.wynnadhoc.utils.ChatMessageUtils;
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
            .connectTimeout(Duration.ofSeconds(CrowdsourceMain.TIMEOUT))
            .build();

    public static CompletableFuture<List<LootChest>> getChests(boolean onlyVerified) {
        String url = CrowdsourceMain.SERVER_URL + "/api/chests/" + (onlyVerified ? "known" : "all");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(CrowdsourceMain.TIMEOUT))
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
                        WynnAdhocClient.LOGGER.warn("Failed to get chests: " + response.statusCode());
                        return new ArrayList<>();
                    }
                })
                .exceptionally(ex -> {
                    WynnAdhocClient.LOGGER.warn("Error getting chests: ");
                    ex.printStackTrace();
                    return new ArrayList<>();
                });
    }

    public static int submitChests() {
        if (!config().syncChests) return 0;
        String url = CrowdsourceMain.SERVER_URL + "/api/chests/submit";
        int count = newChests.size();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(CrowdsourceMain.TIMEOUT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(newChests)))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .whenComplete((response, ex) -> {
                    if (ex != null) {
                        WynnAdhocClient.LOGGER.info(Debug.Type.SERVER, "Failed to submit chests: " + ex.getMessage());
                    }

                    if (response.statusCode() == 200) {
                        WynnAdhocClient.LOGGER.info(Debug.Type.SERVER, "submit" + count + "chests");
                        newChests.clear();
                    } else {
                        WynnAdhocClient.LOGGER.info(Debug.Type.SERVER, "Failed to submit chests: " + response.statusCode());
                    }
                });
        return count;
    }

    public static void submitChests(ClientPlayNetworkHandler netHand, MinecraftClient client) {
        submitChests();
    }

    public static int submitAllChests() {
        if (!config().syncChests) return 0;
        String url = CrowdsourceMain.SERVER_URL + "/api/chests/submit";

        List<LootChest> allChests = ConfigManager.INSTANCE.getChests().entrySet().stream()
                .map((e) ->
                        new LootChest(e.getKey().getX(), e.getKey().getY(), e.getKey().getZ(), e.getValue().tier)
                )
                .toList();

        int count = allChests.size();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(CrowdsourceMain.TIMEOUT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(allChests)))
                .build();

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
        return count;
    }

    public static void register(CommandRegistrationEvent event) {
        event.register(ClientCommandManager.literal("Download")
                .then(ClientCommandManager.literal("chests")
                        .executes(ctx -> {
                            CompletableFuture<Integer> amount = ConfigManager.loadChestsFromServer();
                            amount.whenComplete((c, ex) -> {
                                if (ex == null) {
                                    ChatMessageUtils.sendChatMessage("loaded " + c + " new chests from server");
                                } else {
                                    ChatMessageUtils.sendChatMessage("failed to load chests from server " + ex.getMessage());
                                }
                            });
                            return 1;
                        })
                )
        );
        event.register(ClientCommandManager.literal("Upload")
                .then(ClientCommandManager.literal("chestsNew")
                        .executes(ctx -> {
                            int amount = ChestCrowdsource.submitChests();
                            ChatMessageUtils.sendChatMessage("send " + amount + " chests locations to server");
                            return 1;
                        })
                )
        );
        event.register(ClientCommandManager.literal("Upload")
                .then(ClientCommandManager.literal("chestsAll")
                        .executes(ctx -> {
                            int amount = ChestCrowdsource.submitAllChests();
                            ChatMessageUtils.sendChatMessage("send " + amount + " chests locations to server");
                            return 1;
                        })
                )
        );
    }
}
