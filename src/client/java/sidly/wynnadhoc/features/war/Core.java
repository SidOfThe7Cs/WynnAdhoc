package sidly.wynnadhoc.features.war;

import net.minecraft.client.MinecraftClient;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.WarConfig;
import sidly.wynnadhoc.event.ChatMessageEvent;
import sidly.wynnadhoc.utils.DebugWindow;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Core {
    private static WarConfig config() { return ConfigManager.INSTANCE.config.war; }

    public static void onChatMessage(ChatMessageEvent event) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        //loadouts
        Pattern loadoutPattern = Pattern.compile("^(.*?) applied the loadout (.*?) on (.*?)$");
        Matcher loadoutMatcher = loadoutPattern.matcher(event.asciiOnlyMessage);
        if (loadoutMatcher.find()) {
            String playerName = loadoutMatcher.group(1).trim();
            String loadoutName = loadoutMatcher.group(2).trim();
            String territoryPart = loadoutMatcher.group(3).trim();

            // Split territories by commas or "and" (with optional spaces)
            String[] territories = territoryPart.split("\\s*(?:,|and)\\s*");

            for (String territoryName : territories) {
                territoryName = territoryName.trim();
                if (territoryName.isEmpty()) continue;

                Territory territory = DB.ownedTerritories.get(territoryName);
                if (territory != null) {
                    territory.setMarkedAsUnknown(true);
                    DebugWindow.getInstance().log(DebugWindow.Priority.INFO, "marked " + territoryName + " as unknown (loadout: " + loadoutName + ")");
                } else {
                    DebugWindow.getInstance().log(DebugWindow.Priority.WARNING, "unknown territory: " + territoryName);
                }
            }
        }

        // player changed a single upgrade
        Pattern singleUpgradePattern = Pattern.compile("^(.*?) set (.*?) (?:upgrade|bonus) to level (\\d+) on (.*?)$");
        Matcher singleUpgradeMatcher = singleUpgradePattern.matcher(event.asciiOnlyMessage);
        if (singleUpgradeMatcher.find()) {
            String playerName = singleUpgradeMatcher.group(1).trim();
            String upgradeName = singleUpgradeMatcher.group(2).trim();
            int level = Integer.parseInt(singleUpgradeMatcher.group(3).trim());
            String territoryName = singleUpgradeMatcher.group(4).trim();

            // if it wasnt you
            if (!client.player.getName().getString().equals(playerName)) {
                Territory territory = DB.ownedTerritories.get(territoryName);
                if (territory != null) {
                    territory.setUpgrade(upgradeName, level);
                    DebugWindow.getInstance().log(DebugWindow.Priority.INFO, "set " + upgradeName + " to " + level);
                }
            }
        }
        //player removed a single upgrade
        Pattern removedPattern = Pattern.compile("^(.*?) removed (.*?) (?:upgrade|bonus) from (.*?)$");
        Matcher removedMatcher = removedPattern.matcher(event.message);
        if (removedMatcher.find()) {
            String playerName = removedMatcher.group(1).trim();
            String upgradeName = removedMatcher.group(2).trim();
            String territoryName = removedMatcher.group(3).trim();

            // if it wasnt you
            if (!client.player.getName().getString().equals(playerName)) {
                Territory territory = DB.ownedTerritories.get(territoryName);
                if (territory != null) {
                    territory.setUpgrade(upgradeName, 0);
                    DebugWindow.getInstance().log(DebugWindow.Priority.INFO, "set " + upgradeName + " to " + 0);
                }
            }
        }
        //player changed multiple bonuses
        Pattern bonusChangePattern = Pattern.compile("^(.*?) changed (\\d+) bonuses on (.*?)$");
        Matcher bonusChangeMatcher = bonusChangePattern.matcher(event.message);

        if (bonusChangeMatcher.find()) {
            String playerName = bonusChangeMatcher.group(1).trim();
            int bonusCount = Integer.parseInt(bonusChangeMatcher.group(2).trim());
            String territoryName = bonusChangeMatcher.group(3).trim();

            // if it wasnt you
            if (!client.player.getName().getString().equals(playerName)) {
                Territory territory = DB.ownedTerritories.get(territoryName);
                if (territory != null) {
                    territory.setMarkedAsUnknown(true);
                    DebugWindow.getInstance().log(DebugWindow.Priority.INFO, "marked " + territoryName + " as unknown");
                }
            }
        }
        // you captured a territory
        Pattern takeoverPattern = Pattern.compile("^You have taken control of (.*?) from \\[(.*?)]! Use /guild territory to defend this territory\\.$");
        Matcher takeoverMatcher = takeoverPattern.matcher(event.asciiOnlyMessage);

        if (takeoverMatcher.find()) {
            String territoryName = takeoverMatcher.group(1).trim();
            String guildName = takeoverMatcher.group(2).trim();

            DB.ownedTerritories.put(territoryName, DB.allTerritories.get(territoryName));
            DebugWindow.getInstance().log(DebugWindow.Priority.INFO, "added " + territoryName + " as owned");
        }
        // you lost a territory
        Pattern guildTakeoverPattern = Pattern.compile("^\\[(.*?)] has taken control of (.*?)!$");
        Matcher guildTakeoverMatcher = guildTakeoverPattern.matcher(event.asciiOnlyMessage);

        if (guildTakeoverMatcher.find()) {
            String guildName = guildTakeoverMatcher.group(1).trim();
            String territoryName = guildTakeoverMatcher.group(2).trim();

            DB.ownedTerritories.remove(territoryName);
            DebugWindow.getInstance().log(DebugWindow.Priority.INFO, "removed " + territoryName + " from owned");
        }

    }

    public static boolean shouldShowResourceOverlay() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (config().showResourceOverlay && client != null && client.currentScreen != null) {
            String title = client.currentScreen.getTitle().getString();
            int colonIndex = title.indexOf(':');
            if (colonIndex != -1) {
                title = title.substring(0, colonIndex).trim();
            }
            if (DB.allTerritories.containsKey(title)) {
                return true;
            } else if (title.equals(DB.GUILD_NAME)) {
                return true;
            } else return title.equals("Territory Management");
        }
        return false;
    }

    public static String updateResourceDisplay() {
        int oreProd = 0;
        int oreConsume = 0;

        int cropsProd = 0;
        int cropsConsume = 0;

        int woodProd = 0;
        int woodConsume = 0;

        int fishProd = 0;
        int fishConsume = 0;

        int emeraldProd = 0;
        int emeraldConsume = 0;

        for (Map.Entry<String, Territory> entry : DB.ownedTerritories.entrySet()) {
            Territory territory = entry.getValue();

            emeraldProd += territory.getEmeraldsPerHourProd();
            emeraldConsume += territory.getEmeraldsPerHourConsume();

            oreProd += territory.getOrePerHourProd();
            oreConsume += territory.getOrePerHourConsume();

            woodProd += territory.getWoodPerHourProd();
            woodConsume += territory.getWoodPerHourConsume();

            fishProd += territory.getFishPerHourProd();
            fishConsume += territory.getFishPerHourConsume();

            cropsProd += territory.getCropsPerHourProd();
            cropsConsume += territory.getCropsPerHourConsume();
        }

        return String.valueOf(DB.getDisplay("§a", ResourceType.Emeralds, emeraldConsume, emeraldProd)) +
                DB.getDisplay("§f", ResourceType.Ore, oreConsume, oreProd) +
                DB.getDisplay("§6", ResourceType.Wood, woodConsume, woodProd) +
                DB.getDisplay("§b", ResourceType.Fish, fishConsume, fishProd) +
                DB.getDisplay("§e", ResourceType.Crops, cropsConsume, cropsProd);
    }

    public static void onWarResourceDisplayClick() {
        DB.parseTerritoryScreen(MinecraftClient.getInstance().currentScreen, true);
    }
}
