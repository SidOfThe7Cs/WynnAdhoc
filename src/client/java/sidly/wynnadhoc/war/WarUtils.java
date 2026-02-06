package sidly.wynnadhoc.war;

import net.minecraft.client.MinecraftClient;
import sidly.wynnadhoc.event.ChatMessageEvent;
import sidly.wynnadhoc.utils.DebugWindow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WarUtils {

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
                    DebugWindow.getInstance().log(DebugWindow.Priority.INFO,"marked " + territoryName + " as unknown (loadout: " + loadoutName + ")");
                } else {
                    DebugWindow.getInstance().log(DebugWindow.Priority.WARNING,"unknown territory: " + territoryName);
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
                    DebugWindow.getInstance().log(DebugWindow.Priority.INFO,"set " + upgradeName + " to " + level);
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
                    DebugWindow.getInstance().log(DebugWindow.Priority.INFO,"set " + upgradeName + " to " + 0);
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
                    DebugWindow.getInstance().log(DebugWindow.Priority.INFO,"marked " + territoryName + " as unknown");
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
            DebugWindow.getInstance().log(DebugWindow.Priority.INFO,"added " + territoryName + " as owned");
        }
        // you lost a territory
        Pattern guildTakeoverPattern = Pattern.compile("^\\[(.*?)] has taken control of (.*?)!$");
        Matcher guildTakeoverMatcher = guildTakeoverPattern.matcher(event.asciiOnlyMessage);

        if (guildTakeoverMatcher.find()) {
            String guildName = guildTakeoverMatcher.group(1).trim();
            String territoryName = guildTakeoverMatcher.group(2).trim();

            DB.ownedTerritories.remove(territoryName);
            DebugWindow.getInstance().log(DebugWindow.Priority.INFO,"removed " + territoryName + " from owned");
        }

    }
}
