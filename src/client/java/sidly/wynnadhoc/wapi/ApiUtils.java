package sidly.wynnadhoc.wapi;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.wapi.item.*;
import sidly.wynnadhoc.wapi.item.enums.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ApiUtils {
    public static final UnknownFieldTracker fieldTracker = new UnknownFieldTracker();
    private static final Map<String, WynnItem> db = new HashMap<>();
    public static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(AttackSpeed.class, EnumUtils.enumDeserializer(AttackSpeed.class, fieldTracker))
            .registerTypeAdapter(Base.class, EnumUtils.enumDeserializer(Base.class, fieldTracker))
            .registerTypeAdapter(DropMetaEvent.class, EnumUtils.enumDeserializer(DropMetaEvent.class, fieldTracker))
            .registerTypeAdapter(DropMetaName.class, EnumUtils.enumDeserializer(DropMetaName.class, fieldTracker))
            .registerTypeAdapter(DropMetaType.class, EnumUtils.enumDeserializer(DropMetaType.class, fieldTracker))
            .registerTypeAdapter(DropRestriction.class, EnumUtils.enumDeserializer(DropRestriction.class, fieldTracker))
            .registerTypeAdapter(Element.class, EnumUtils.enumDeserializer(Element.class, fieldTracker))
            .registerTypeAdapter(Emblem.class, EnumUtils.enumDeserializer(Emblem.class, fieldTracker))
            .registerTypeAdapter(Restriction.class, EnumUtils.enumDeserializer(Restriction.class, fieldTracker))
            .registerTypeAdapter(SubType.class, EnumUtils.enumDeserializer(SubType.class, fieldTracker))
            .registerTypeAdapter(Tier.class, EnumUtils.enumDeserializer(Tier.class, fieldTracker))
            .registerTypeAdapter(ItemType.class, EnumUtils.enumDeserializer(ItemType.class, fieldTracker))
            .registerTypeAdapter(Identification.class, EnumUtils.enumDeserializer(Identification.class, fieldTracker))
            .registerTypeAdapter(MajorID.class, EnumUtils.enumDeserializer(MajorID.class, fieldTracker))
            .registerTypeAdapter(Quest.class, EnumUtils.enumDeserializer(Quest.class, fieldTracker))
            .registerTypeAdapter(IconFormat.class, EnumUtils.enumDeserializer(IconFormat.class, fieldTracker))
            .registerTypeAdapter(CraftingStation.class, EnumUtils.enumDeserializer(CraftingStation.class, fieldTracker))

            .registerTypeAdapter(IdentificationData.class, IdentificationData.getTypeAdaptor())
            .registerTypeAdapter(IconValue.class, IconValue.getTypeAdaptor())
            .registerTypeAdapter(new TypeToken<Set<Coords>>() {
            }.getType(), Coords.getTypeAdaptor())
            .registerTypeAdapter(new TypeToken<Set<DropMetaType>>() {
            }.getType(), DropMeta.getTypeAdaptor())
            .create();

    public static Map<String, WynnItem> getItemDatabase() {
        return db.isEmpty() ? getItemDatabaseFromApi() : db;
    }

    public static WynnItem getItemInfo(String name) {
        getItemDatabase();
        return db.get(name);
    }

    public static Set<WynnItem> getItemWhere(Function<WynnItem, Boolean> condition) {
        getItemDatabase();
        return db.values().stream().filter(condition::apply).collect(Collectors.toSet());
    }

    private static Map<String, WynnItem> getItemDatabaseFromApi() {
        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.wynncraft.com/v3/item/database?fullResult"))
                    .header("Authorization", "Bearer " + ConfigManager.INSTANCE.getToken())
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            if (status != 200) {
                WynnAdhocClient.LOGGER.error("failed to get item database from api, error code: " + status);
                return new HashMap<>();
            }

            String body = response.body().trim();
            if (!(body.startsWith("{") || body.startsWith("["))) {
                WynnAdhocClient.LOGGER.error("failed to get item database from api, invalid json: " + body);
                return new HashMap<>();
            }


            // create a new gson with a custom deserializer and the custom type adapters
            Gson gson = new GsonBuilder().registerTypeAdapter(WynnItem.class, new ItemDeserializer(GSON)).create();

            JsonElement jsonElement = JsonParser.parseString(body);
            if (jsonElement.isJsonArray()) {
                jsonElement.getAsJsonArray().forEach(element -> {
                    if (element.isJsonObject()) {
                        JsonObject obj = element.getAsJsonObject();
                        if (obj.has("internalName")) {
                            db.put(obj.get("internalName").getAsString(), gson.fromJson(obj, WynnItem.class));
                        } else WynnAdhocClient.LOGGER.warn("found item without an internal name: " + obj);
                    }
                });
            } else WynnAdhocClient.LOGGER.warn("item db returned not array: " + jsonElement);

            fieldTracker.printReport();
            return db;

        } catch (IOException | InterruptedException e) {
            WynnAdhocClient.LOGGER.error("failed to get item database from api: " + e.getMessage() + " " + e.getCause());
            return new HashMap<>();
        }
    }
}
