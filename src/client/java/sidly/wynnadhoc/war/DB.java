package sidly.wynnadhoc.war;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wynntils.models.items.items.gui.TerritoryItem;
import com.wynntils.screens.territorymanagement.TerritoryManagementScreen;
import com.wynntils.utils.type.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import sidly.wynnadhoc.event.ScreenRenderEvent;
import sidly.wynnadhoc.mixin.client.accessors.TerritoryManagementScreenAccessor;
import sidly.wynnadhoc.utils.DebugWindow;
import sidly.wynnadhoc.utils.FormatUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DB {
    public static final String GUILD_NAME = "Chiefs Of Corkus";
    public static final String GUILD_PREFIX = "HOC";

    public static Map<String, Territory> allTerritories;
    public static Map<String, Territory> ownedTerritories = new HashMap<>();
    // String guild prefix mapped to a map that maps territory names to territory
    public static Map<String, Map<String, Territory>> ApiTerritoryData = new HashMap<>();
    public static Map<ResourceType, Integer> currentStoredResources = new HashMap<>();
    public static Map<ResourceType, Integer> storageCaps = new HashMap<>();
    public static List<TerritoryItem> wynntilsTerritoryItems;

    static {
        Map<String, Territory> defaults = new HashMap<>();

        try {
            Gson gson = new Gson();
            InputStream input = DB.class.getClassLoader().getResourceAsStream("assets/wynntools/territoryDefaults.json"); // TODO make exist
            Reader reader = new InputStreamReader(input);


            Type listType = new TypeToken<List<JsonTerritoryData>>() {
            }.getType();
            List<JsonTerritoryData> jsonData = gson.fromJson(reader, listType);

            for (JsonTerritoryData data : jsonData) {
                int emeralds = 0;
                int ore = 0;
                int crops = 0;
                int fish = 0;
                int wood = 0;

                for (String resource : data.resources) {
                    int amount = extractNumber(resource);
                    if (resource.contains("Emerald")) {
                        emeralds = amount;
                    } else if (resource.contains("Ore")) {
                        ore = amount;
                    } else if (resource.contains("Crop")) {
                        crops = amount;
                    } else if (resource.contains("Fish")) {
                        fish = amount;
                    } else if (resource.contains("Wood")) {
                        wood = amount;
                    }
                }

                Territory territory = new Territory(data.territoryName, emeralds, ore, crops, fish, wood);
                defaults.put(data.territoryName, territory);
            }

            allTerritories = Collections.unmodifiableMap(defaults);
        } catch (Exception e) {
            e.printStackTrace();
            allTerritories = Map.of();
        }
    }

    // Helper method to extract number from string like "+18000 Emeralds"
    private static int extractNumber(String s) {
        // Remove '+' and non-digit chars, then parse integer
        String digits = s.replaceAll("[^0-9]", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }

    public static void updateFromApi() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.wynncraft.com/v3/guild/list/territory"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse response with Gson
            Gson gson = new Gson();
            Type type = new TypeToken<TerritoryApiResponse>() {
            }.getType();
            TerritoryApiResponse apiData = gson.fromJson(response.body(), type);

            if (apiData == null) {
                System.err.println("apiData was null");
                return;
            }

            // Update your territoryData map
            for (Map.Entry<String, TerritoryInfo> entry : apiData.entrySet()) {
                String territoryName = entry.getKey();
                TerritoryInfo info = entry.getValue();

                Territory territory = allTerritories.get(territoryName);

                if (territory == null) {
                    System.err.println("territory: " + territoryName + " not found in allTerritories");
                    continue;
                }

                // Update holder and time held
                String guildPrefix = (info.guild != null) ? info.guild.prefix : "Nobody";
                territory.setHolder(guildPrefix);
                territory.setHeldSince(FormatUtils.timeSinceIso(info.acquired, ChronoUnit.SECONDS));

                ApiTerritoryData.computeIfAbsent(guildPrefix, k -> new HashMap<>());
                ApiTerritoryData.get(guildPrefix).put(territoryName, territory);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void parseTerritoryScreen(Screen screen, boolean forceUpdate) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || screen == null) return;

        if (screen instanceof TerritoryManagementScreen territoryScreen) {
            String title = territoryScreen.getTitle().getString();

            if (title.equals("Territory Management")) {
                List<TerritoryItem> territoryItems = ((TerritoryManagementScreenAccessor) territoryScreen).getTerritoryItems().stream().map(Pair::b).toList();
                wynntilsTerritoryItems = territoryItems;
                ownedTerritories.clear();
                for (TerritoryItem wynntilsTerritory : territoryItems) {
                    if (allTerritories.containsKey(wynntilsTerritory.getName())) {
                        Territory territory = allTerritories.get(wynntilsTerritory.getName());
                        territory.setHQ(wynntilsTerritory.isHeadquarters());
                        territory.setTreasuryBonus(wynntilsTerritory.getTreasuryBonus());

                        if (territory.isMarkedAsUnknown() || forceUpdate) {
                            territory.parseFromWynntils(wynntilsTerritory);
                            DebugWindow.getInstance().log(DebugWindow.Priority.INFO, "parsed info for " + wynntilsTerritory.getName());
                        }

                        ownedTerritories.put(wynntilsTerritory.getName(), territory);
                    } else
                        DebugWindow.getInstance().log(DebugWindow.Priority.WARNING, "couldnt find territory " + wynntilsTerritory.getName());
                }

                Territory hq = getHQ();
                if (hq != null) {
                    DB.storageCaps = hq.getStorageCaps();
                }
            }
        }
    }

    //called every rendered frame
    public static void parseScreen(ScreenRenderEvent event) {

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;


        if (event.screen instanceof GenericContainerScreen containerScreen) {
            String title = containerScreen.getTitle().getString();
            if (title.equals(GUILD_NAME + ": Manage")) {
                // get info from the guild output diamond
                Slot slot = containerScreen.getScreenHandler().slots.get(17);
                List<Text> tooltip = FormatUtils.getTooltip(slot.getStack());
                if (tooltip.isEmpty()) return;

                String firstLine = tooltip.getFirst().getString();
                if (!firstLine.contains("Guild Output")) {
                    //DebugWindow.getInstance().log("wrong item in slot 17 " + firstLine);
                    return;
                }

                for (int i = 0; i < tooltip.size(); i++) {
                    String line = tooltip.get(i).getString();

                    // Match any resource line like "+9000 Emeralds per Hour"
                    if (line.contains("per Hour")) {
                        // Identify which resource it is
                        ResourceType resource = null;
                        if (line.contains("Emeralds")) resource = ResourceType.Emeralds;
                        else if (line.contains("Ore")) resource = ResourceType.Ore;
                        else if (line.contains("Wood")) resource = ResourceType.Wood;
                        else if (line.contains("Fish")) resource = ResourceType.Fish;
                        else if (line.contains("Crops")) resource = ResourceType.Crops;

                        // Get next line safely
                        if (resource != null && i + 1 < tooltip.size()) {
                            String next = tooltip.get(i + 1).getString();

                            // Find the number before the "/"
                            Matcher m = Pattern.compile(".*?(\\d+)\\s*/").matcher(next);
                            if (m.find()) {
                                int value = Integer.parseInt(m.group(1));
                                //DebugWindow.getInstance().log("found value " + value + " for " + resource + " in line " + next);
                                currentStoredResources.put(resource, value);
                            }
                        }
                    }
                }
            }
            if (title.equals(GUILD_NAME + ": Territories")) {

            /*
                ownedTerritories.clear();
                Inventory inventory = containerScreen.getScreenHandler().getInventory();

                for (int i = 0; i < inventory.size(); i++) {
                    ItemStack stack = inventory.getStack(i);
                    if (!stack.isEmpty()) {
                        String name = ChatMessageUtils.removeColorCodes(stack.getName().getString());
                        boolean hq = name.endsWith(" (HQ)");
                        String cleanedName = hq ? name.substring(0, name.length() - 5).trim() : name.trim();
                        cleanedName = ChatMessageUtils.removeAllBut(cleanedName, "a-zA-Z \\-'");

                        if (allTerritories.containsKey(cleanedName)) {
                            ownedTerritories.put(cleanedName, allTerritories.get(cleanedName));
                            //System.out.println("added " + cleanedName + " to owned territories");
                        }

                        Pattern treasuryBonusPattern = Pattern.compile("Treasury Bonus: (\\d+(?:\\.\\d+)?)%");
                        Pattern upgradePattern = Pattern.compile("- (.+?)\\[Lv\\. (\\d{1,2})]");
                        List<Text> tooltip = stack.getTooltip(Item.TooltipContext.DEFAULT, client.player, TooltipType.BASIC);
                        double treasuryBonus = 0.0;

                        for (Text text : tooltip) {
                            String line = ChatMessageUtils.removeColorCodes(text.getString()).replaceAll("[^a-zA-Z1-9. \\-\\[\\]']", "");
                            //System.out.println(line);

                            Matcher treasuryMatcher = treasuryBonusPattern.matcher(line);
                            if (treasuryMatcher.find()) {
                                treasuryBonus = Double.parseDouble(treasuryMatcher.group(1));
                                //System.out.println("treasury bonus found: " + treasuryBonus);
                                continue;
                            }

                            Territory territory = ownedTerritories.get(cleanedName);
                            if (territory != null && territory.isMarkedAsUnknown()) {
                                Matcher upgradeMatcher = upgradePattern.matcher(line);
                                if (upgradeMatcher.find()) {
                                    String upgradeName = upgradeMatcher.group(1).trim(); // e.g., "Tower Multi-Attacks"
                                    int level = Integer.parseInt(upgradeMatcher.group(2)); // e.g., 1
                                    ownedTerritories.get(cleanedName).setUpgrade(upgradeName, level);
                                }

                                ownedTerritories.get(cleanedName).setMarkedAsUnknown(false);

                                ChatMessageUtils.sendChatMessage(cleanedName + " was marked as unknown so upgrades were parsed");
                            }
                        }

                        //Utils.printItemData(stack);
                    }
                }
                return;
             */
            }


            int colonIndex = title.indexOf(':');
            String name = "";
            String screenType = "";

            if (colonIndex != -1) {
                name = title.substring(0, colonIndex).trim();
                screenType = title.substring(colonIndex + 1).trim();
            }

            if (allTerritories.containsKey(name)) {
                int size = containerScreen.getScreenHandler().getInventory().size();
                if (screenType.equals("Guild Tower")) {
                    for (int i = 0; i < size; i++) {
                        Territory terr = ownedTerritories.get(name);
                        if (terr != null) {
                            Upgrade upgrade = terr.towerUpgrades.upgrades.getBySlot(i);
                            if (upgrade != null) {
                                String upgradeName = containerScreen.getScreenHandler().getSlot(i).getStack().getName().getString();

                                Pattern levelPattern = Pattern.compile("\\[Lv\\. (\\d{1,2})]"); // Matches [Lv. 0] to [Lv. 99]
                                Matcher matcher = levelPattern.matcher(upgradeName);
                                int lvl = -1; // Default or error value
                                if (matcher.find()) {
                                    lvl = Integer.parseInt(matcher.group(1));
                                }
                                if (lvl >= 0) {
                                    upgrade.setStackSize(lvl);
                                    //System.out.println("set " + upgrade.getName() + " to level " + lvl);
                                }
                            }
                        }
                    }
                } else if (screenType.equals("Bonus")) {
                    for (int i = 0; i < size; i++) {
                        Territory terr = ownedTerritories.get(name);
                        if (terr != null) {
                            Upgrade upgrade = terr.bonusUpgrades.upgrades.getBySlot(i);
                            if (upgrade != null) {
                                String upgradeName = containerScreen.getScreenHandler().getSlot(i).getStack().getName().getString();

                                Pattern levelPattern = Pattern.compile("\\[Lv\\. (\\d{1,2})]"); // Matches [Lv. 0] to [Lv. 99]
                                Matcher matcher = levelPattern.matcher(upgradeName);
                                int lvl = -1; // Default or error value
                                if (matcher.find()) {
                                    lvl = Integer.parseInt(matcher.group(1));
                                }
                                if (lvl >= 0) {
                                    upgrade.setStackSize(lvl);
                                    //System.out.println("set " + upgrade.getName() + " to level " + lvl);
                                }
                            }
                        }
                    }
                } else {
                    // we are in the main menu of the territory
                }
            }
        }
    }

    private static double getPercent(int consume, int produce) {
        return Math.round((consume / (double) produce) * 1000) / 10.0;
    }

    public static StringBuilder getDisplay(String colorCode, ResourceType type, int consume, int produce) {
        Integer resourceStored = DB.currentStoredResources.get(type);
        Integer storageCap = DB.storageCaps.get(type);

        StringBuilder sb = new StringBuilder();
        sb.append(colorCode)
                .append(type.name())
                .append(" ")
                .append(consume)
                .append("/")
                .append(produce)
                .append(" (")
                .append(produce == 0 ? "∞" : getPercent(consume, produce))
                .append("%) ");

        if (produce > consume) {
            if (resourceStored != null && storageCap != null) {
                int remainingToFill = storageCap - resourceStored;
                int netProduction = produce - consume;
                if (netProduction > 0 && remainingToFill > 0) {
                    double hoursToFill = remainingToFill / (double) netProduction;
                    //DebugWindow.getInstance().log("hoursToFull = " + hoursToFill);
                    String time = FormatUtils.formatTime(hoursToFill, ChronoUnit.HOURS);
                    sb.append("full in ").append(time);
                }
            } else
                DebugWindow.getInstance().log(DebugWindow.Priority.ERROR, "null found: stored " + resourceStored + "cap " + storageCap);
        } else if (consume > produce) {
            if (resourceStored != null && storageCap != null) {
                int netConsumption = consume - produce;
                if (netConsumption > 0 && resourceStored > 0) {
                    double hoursToEmpty = resourceStored / (double) netConsumption;
                    //DebugWindow.getInstance().log("hoursToEmpty = " + hoursToEmpty);
                    String time = FormatUtils.formatTime(hoursToEmpty, ChronoUnit.HOURS);
                    sb.append("empty in ").append(time);
                }
            } else
                DebugWindow.getInstance().log(DebugWindow.Priority.ERROR, "null found: stored " + resourceStored + "cap " + storageCap);
        }
        sb.append("\n");

        return sb;
    }

    public static Territory getHQ() {
        for (Territory terr : ownedTerritories.values()) {
            if (terr.isHQ()) {
                return terr;
            }
        }
        return null;
    }

    // TODO update display
    public static void onClientTick(MinecraftClient client) {
        // might as well do this here so its only called 20 times per second
        //Config.updateHudElement(HudElements.War_Resources); // only updates when visible
    }

    public static class TerritoryApiResponse extends HashMap<String, TerritoryInfo> {
        // each key is the territory name, mapped to a TerritoryInfo
    }

    public static class TerritoryInfo {
        public GuildInfo guild;
        public String acquired;
        public Location location;
    }

    public static class GuildInfo {
        public String uuid;
        public String name;
        public String prefix;
    }

    public static class Location {
        public List<Integer> start;
        public List<Integer> end;
    }

}
