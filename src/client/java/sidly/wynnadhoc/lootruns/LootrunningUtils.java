package sidly.wynnadhoc.lootruns;

import com.wynntils.core.components.Models;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.LootrunData;
import sidly.wynnadhoc.event.*;
import sidly.wynnadhoc.lootruns.enums.*;
import sidly.wynnadhoc.utils.ChatMessageUtils;
import sidly.wynnadhoc.utils.DebugWindow;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LootrunningUtils {

    public static LootrunData getCurrentLootrunData() {
        String uuid = Models.Character.getId();
        return ConfigManager.INSTANCE.getLootrun(uuid);
    }

    // this is the actual text displays rendered in the world cleared every tick
    public static Map<BeaconColor, Integer> currentBeaconOptionsFromWaypoints = new HashMap<>();

    // i just dont care right now (or ever) its both broken and useless
    public static int mobHealthIncrease = 0;
    public static int mobResistanceIncrease = 0;
    public static int mobDamageIncrease = 0;
    public static int mobAttackSpeedIncrease = 0;
    public static int mobSpeedIncrease = 0;
    public static void addMobHealth(int amount) {
        mobHealthIncrease += amount;
        // TODO update display
    }
    public static void addMobResistance(int amount) {
        //System.out.println(" added res: " + amount);
        mobResistanceIncrease += amount;
        // TODO update display
    }
    public static void addMobDamage(int amount) {
        //System.out.println(" added dam: " + amount);
        mobDamageIncrease += amount;
        // TODO update display
    }
    public static void addMobAttackSpeed(int amount) {
        //System.out.println(" added attk speed: " + amount);
        mobAttackSpeedIncrease += amount;
        // TODO update display
    }
    public static void addMobSpeed(int amount) {
        //System.out.println(" added walk speed: " + amount);
        mobSpeedIncrease += amount;
        // TODO update display
    }

    public static Pattern curseBuffPattern = Pattern.compile("\\[([+-])(\\d+)% (Mob|Enemy) (.+?)]");
    public static Pattern beaconRerollPattern = Pattern.compile("\\((\\d+) rerolls left\\)");
    public static Pattern endPullsPattern = Pattern.compile("\\[\\+?(\\d+) Reward Pulls?]");
    public static Pattern sacrificePattern = Pattern.compile("\\[\\+(\\d+) Reward Sacrifice]");
    public static Pattern endRerollPattern = Pattern.compile("\\[\\+(\\d+) End Reward Reroll]");


    public static void checkIfBeacon(ForEachEntityEvent event){
        // we can also add type detection by checking the l;ast charactor in the idsplay name

        if (event.entity instanceof DisplayEntity.TextDisplayEntity textDisplay) {

            Text rootText = textDisplay.getText(); // Your root text component
            List<Text> siblings = rootText.getSiblings();

            // 1. Check if the first sibling is a "marker"
            if (!siblings.isEmpty() && siblings.get(0).getStyle().getFont() != null && siblings.get(0).getStyle().getFont().toString().equals("minecraft:marker")) {

                // 3. Extract the distance
                int distance = -1;
                if (siblings.size() >= 3) {
                    Text distanceText = siblings.get(2);
                    String distanceStr = distanceText.getString().split(" ")[0]; // "246m"
                    distance = Integer.parseInt(distanceStr.replaceAll("[^0-9]", "")); // 246

                    //System.out.println("Distance: " + distanceStr);
                }


                // 2. Get the color
                Text markerSibling = siblings.get(0);
                for (Text nested : markerSibling.getSiblings()) {
                    if (nested.getStyle().getColor() != null) {
                        int color = nested.getStyle().getColor().getRgb();

                        BeaconColor baseColor;
                        switch (color){
                            case 0x5C5CE6:
                                baseColor = BeaconColor.Blue;
                                break;
                            case 0xFF00FF:
                                baseColor = BeaconColor.Purple;
                                break;
                            case 0xFFFF33:
                                baseColor = BeaconColor.Yellow;
                                break;
                            case 0x55FFFF:
                                baseColor = BeaconColor.Aqua;
                                break;
                            case 0xff9500:
                                baseColor = BeaconColor.Orange;
                                break;
                            case 0xff80:
                                baseColor = BeaconColor.Green;
                                break;
                            case 0x808080:
                                baseColor = BeaconColor.DarkGrey;
                                break;
                            case 0xffffff:
                                baseColor = BeaconColor.White;
                                break;
                            case 0xbfbfbf:
                                baseColor = BeaconColor.Grey;
                                break;
                            case 0xff0000:
                                baseColor = BeaconColor.Red;
                                break;
                            case 0xf000:
                                baseColor = BeaconColor.Rainbow;
                                break;
                            case 0xf010:
                                baseColor = BeaconColor.Crimson;
                                break;
                            default:
                                System.out.println("unrecognized beacon color: " + Integer.toHexString(color) + " distance: " + distance);
                                return;
                        }
                        if (!currentBeaconOptionsFromWaypoints.containsKey(baseColor)){
                            currentBeaconOptionsFromWaypoints.put(baseColor, distance);
                        }
                        //String symbol = nested.getString();
                        //symbol = Utils.convertCustomCharacterToUnicode(symbol);
                        //System.out.printf("Symbol: %s (Color: #%06X)\n", symbol, color);
                    }
                }


                // get beacon type
                // Get the nested components within the marker
                List<Text> markerComponents = markerSibling.getSiblings();
                if (markerComponents.size() > 1) {
                    // The symbol is in the second nested component (index 1)
                    Text symbolComponent = markerComponents.get(1);
                    String symbolText = symbolComponent.getString();

                    int lastSymbol = symbolText.codePoints().skip(symbolText.codePointCount(0, symbolText.length()) - 1).findFirst().orElse(-1);
                    String unicodeString = "U+" + String.format("%04X", lastSymbol);
                    String type = "";
                    switch (unicodeString){
                        case "U+E00B":
                            type = "Slay";
                            break;
                        case "U+E00C":
                            type = "Target";
                            break;
                        case "U+E00D":
                            type = "Defend";
                            break;
                        case "U+E00E":
                            type = "Loot";
                            break;
                        case "U+E00F":
                            type = "Destroy";
                            break;
                        default:
                            System.out.printf("Last symbol: " + unicodeString + " Distance: " + distance + " type: " + type + "\n");
                            break;
                    }

                }
            }
        }
    }

    // adds a mission to the active list but active list also contains in progress so called from scoreboard
    public static void addMission(String name){
        for (MissionOptions opt : MissionOptions.values()) {
            if (name.contains(opt.getDisplayName())) {
                boolean alreadyExists = getCurrentLootrunData().getCurrentMissionsActive().stream().anyMatch(existing -> existing.getDisplayName().equals(opt.getDisplayName()));

                if (!alreadyExists) {
                    getCurrentLootrunData().getCurrentMissionsActive().add(opt);
                }

                // TODO update display
            }
        }
    }

    public static void addTrial(String name){
        for (TrialOptions opt : TrialOptions.values()) {
            if (name.contains(opt.getDisplayName())) {
                boolean alreadyExists = getCurrentLootrunData().getCurrentTrialsActive().stream().anyMatch(existing -> existing.getDisplayName().equals(opt.getDisplayName()));

                if (!alreadyExists) {
                    getCurrentLootrunData().getCurrentTrialsActive().add(opt);
                }

                // TODO update display
            }
        }
    }

    public static void onChallengeFailed(){
        LootrunData config = getCurrentLootrunData();

        config.clearActiveBeaconColor();
        config.setAquaStatus(AquaStatus.Inactive);
        config.getBeaconCounts().decreaseRemaining();
        // TODO update display
    }

    public static void onChallengeCompleted(BeaconColor color){
        LootrunData config = getCurrentLootrunData();
        if (color == null) {
            DebugWindow.getInstance().log(DebugWindow.Priority.WARNING,"completed a null beacon smh");
            return;
        }

        // check the availoble beacon options to see if its vibrant or not
        boolean vibrant = false;
        String beaconCompleted = "";
        for (BeaconOptions beacon : getCurrentLootrunData().getCurrentBeaconOptions()){
            if (beacon.getBaseColor().equals(color)){
                beaconCompleted = beacon.getDisplayName();
                break;
            }
        }
        if (beaconCompleted.startsWith("Vibrant")) vibrant = true;

        DebugWindow.getInstance().log(DebugWindow.Priority.INFO,"completed " + (vibrant ? "vibrant " : "") + color + " beacon");

        config.getBeaconCounts().decreaseRemaining();
        config.getBeaconCounts().incrementCount(color);
        switch (color){
            case Yellow:
                getCurrentLootrunData().resetPullsSinceLastYellow();
                break;
            case Purple:
                boolean phobia = (getCurrentLootrunData().getCurrentMissionsActive().contains(MissionOptions.Porphyrophobia)
                        && !ScoreboardUtils.missionInProgress.equals("Porphyrophobia"));
                int pulls = getBeaconMultiplier(1, vibrant);
                getCurrentLootrunData().getEndStats().addEndPulls(phobia ? pulls * 2 : pulls);
                break;
            case Aqua:
                if (vibrant){
                    config.setAquaStatus(AquaStatus.Vibrant);
                }else config.setAquaStatus(AquaStatus.Active);
                break;
            case Orange:
                config.getBeaconCounts().addRemaining(BeaconColor.Orange, getBeaconMultiplier(5, vibrant));
                break;
            case DarkGrey:
                getCurrentLootrunData().getEndStats().addEndPulls(getBeaconMultiplier(3, vibrant));
                break;
            case Red:
                int redToAdd = 3;
                if (vibrant) redToAdd = 5;
                if (config.getAquaStatus().equals(AquaStatus.Vibrant)) redToAdd *= 3;
                else if (config.getAquaStatus().equals(AquaStatus.Active)) redToAdd *= 2;
                config.getBeaconCounts().addRemaining(BeaconColor.Red, redToAdd);
                break;
            case Rainbow:
                int rainbowToAdd = getBeaconMultiplier(10, vibrant);
                config.getBeaconCounts().addRemaining(BeaconColor.Rainbow, rainbowToAdd);
                break;

        }

        if (!color.equals(BeaconColor.Aqua)) config.setAquaStatus(AquaStatus.Inactive);

        config.clearActiveBeaconColor();

        // TODO update display
    }

    public static void completeChaosChallengeCompleted(BeaconOptions beacon){
        DebugWindow.getInstance().log(DebugWindow.Priority.INFO,"complete chaos gave " + beacon.getDisplayName());
        LootrunData config = getCurrentLootrunData();

        boolean vibrant = beacon.getDisplayName().startsWith("Vibrant");
        int multiplier = 1;
        if (vibrant) multiplier = 2;

        BeaconColor color = beacon.getBaseColor();

        switch (color){
            case Purple:
                boolean phobia = (getCurrentLootrunData().getCurrentMissionsActive().contains(MissionOptions.Porphyrophobia)
                        && !ScoreboardUtils.missionInProgress.equals("Porphyrophobia"));
                getCurrentLootrunData().getEndStats().addEndPulls(phobia ? multiplier * 2 : multiplier);
                break;
            case Aqua:
                if (vibrant){
                    config.setAquaStatus(AquaStatus.Vibrant);
                }else config.setAquaStatus(AquaStatus.Active);
                break;
            case Orange:
                config.getBeaconCounts().addRemaining(BeaconColor.Orange, 5 * multiplier);
                break;
            case DarkGrey:
                getCurrentLootrunData().getEndStats().addEndPulls(3 * multiplier);
                break;
            case Red:
                int redToAdd = 3;
                if (vibrant) redToAdd = 5;
                config.getBeaconCounts().addRemaining(BeaconColor.Red, redToAdd);
                break;
            case Rainbow:
                config.getBeaconCounts().addRemaining(BeaconColor.Rainbow, 10 * multiplier);
                break;
        }
    }

    private static int getBeaconMultiplier(int baseValue, boolean vibrant){
        LootrunData config = getCurrentLootrunData();
        int multiplier = 1;
        if (vibrant) multiplier *= 2;
        if (config.getAquaStatus().equals(AquaStatus.Vibrant)) multiplier *= 3;
        else if (config.getAquaStatus().equals(AquaStatus.Active)) multiplier *= 2;
        return baseValue * multiplier;
    }

    public static void onMissionCompleted(String mission) {
        System.out.println("MIssion completed: " + mission);
        switch (mission){
            case "High Roller":
                getCurrentLootrunData().getEndStats().addEndRerolls(1);
                return;
            case "Inner Peace":
                // curses half effective
                return;
            case "Redemption":
                getCurrentLootrunData().getEndStats().addEndSacs(1);
                return;
            case "Interest Scheme":
                getCurrentLootrunData().resetPullsSinceLastYellow();
                return;
        }
    }

    public static void onTrialCompleted(String trial) {
        System.out.println("Trial completed: " + trial);
        switch (trial){
            case "All In":
                // in reality this happens at the end of lootrun not instantly
                getCurrentLootrunData().getEndStats().addEndRerolls(getCurrentLootrunData().getEndStats().getEndSacs() * 2);
                getCurrentLootrunData().getEndStats().clearEndSacs();
                getCurrentLootrunData().getEndStats().addEndSacs(0);
                return;
            case "Hubris", "Warmth Devourer":
                getCurrentLootrunData().getEndStats().addEndRerolls(1);
                getCurrentLootrunData().getEndStats().addEndSacs(1);
                return;
            case "Side Hustle":
                getCurrentLootrunData().getEndStats().addEndRerolls(2);
                return;
            case "Treasury Bill":
                getCurrentLootrunData().getEndStats().addEndPulls((int)(getCurrentLootrunData().getEndStats().getEndPulls() * 0.7));
                return;
            case "Ultimate Sacrifice":
                getCurrentLootrunData().getEndStats().addEndSacs(2);
                return;
        }
    }

    public static void changeStatus(LootrunStatus newStatus) {
        LootrunData config = getCurrentLootrunData();
        LootrunStatus oldStatus = config.getStatus();
        if (oldStatus == newStatus) return;
        //DebugWindow.getInstance().log(DebugWindow.Priority.INFO,"switching lootrun status from " + oldStatus + " to " + newStatus);
        switch (newStatus) {
            case PickingBeacon:
                if (oldStatus == LootrunStatus.NotInLootrun) {
                    getCurrentLootrunData().startLootrun();
                    // TODO update display
                }
                break;
            case InChallenge:
                if (oldStatus == LootrunStatus.PickingBeacon){
                    config.activateBeacon();
                }
                break;
            case ClaimingRewards:
                if (oldStatus == LootrunStatus.InChallenge){
                    onChallengeCompleted(config.getActiveBeaconColor());
                }
                break;
            case NotInLootrun:
                endLootrun();
                break;
        }
        config.setStatus(newStatus);
    }

    private static BlockPos getClosestChest(MinecraftClient client){
        if (client == null || client.player == null) return null;
        Vec3d playerPos = client.player.getEntityPos();
        double shortestDist = Double.MAX_VALUE;
        BlockPos closestChest = null;
        for (Map.Entry<BlockPos, Long> entry : ConfigManager.INSTANCE.getChests().entrySet()) {
            double dist = entry.getKey().getSquaredDistance(playerPos.x, playerPos.y, playerPos.z);
            if (dist < shortestDist) {
                closestChest = entry.getKey();
                shortestDist = dist;
            }
        }
        return closestChest;
    }

    public static int getEffectivePulls(){
        int epulls = 0;
        if (getCurrentLootrunData().getActiveCamp() != null){
            int rrs = getCurrentLootrunData().getEndStats().getEndRerolls();
            int pulls = getCurrentLootrunData().getEndStats().getEndPulls();

            if (getCurrentLootrunData().getActiveCamp().getCamp().isDailyReady()){
                rrs += 1;
                pulls += 10;
            }

            epulls += pulls;
            epulls += getCurrentLootrunData().getActiveCamp().getCamp().getSacs();
            epulls *= (rrs + 1);
        }
        return epulls;
    }

    public static long getTimeTillDailyReset(){
        ZoneId estZone = ZoneId.of("America/New_York");
        ZonedDateTime now = ZonedDateTime.now(estZone);
        ZonedDateTime todayAt12pm = now.withHour(23).withMinute(59).withSecond(59).withNano(999);

        // If it's already past 11 PM, move to tomorrow
        if (now.isAfter(todayAt12pm)) {
            todayAt12pm = todayAt12pm.plusDays(1);
        }
        return (Duration.between(now, todayAt12pm).toMillis());
    }

    public static void endLootrun(){
        LootrunData config = getCurrentLootrunData();

        ScoreboardUtils.clearLootrunData(); // this is data that is cleared every frame anyway
        config.getCurrentMissionsActive().clear();
        config.getActiveCamp().getCamp().justCompleted();
        ConfigManager.INSTANCE.resetLootrun(Models.Character.getId());

        mobHealthIncrease = 0;
        mobSpeedIncrease = 0;
        mobDamageIncrease = 0;
        mobAttackSpeedIncrease = 0;
        mobResistanceIncrease = 0;
    }

    // events TODO make sure all are called

    public static void onClientTick(ClientTickEvent event) {
        if (event.client.world == null || currentBeaconOptionsFromWaypoints.isEmpty()) return;

        // get the waypoint if we might be about to start it
        for (Map.Entry<BeaconColor, Integer> entry : currentBeaconOptionsFromWaypoints.entrySet()) {
            if (entry.getValue() == -1) {
                getCurrentLootrunData().setPossibleActiveBeaconColor(entry.getKey());
                break;
            }
        }
        currentBeaconOptionsFromWaypoints.clear();
    }

    public static void onBlockEntityLoad(BlockEntityLoadedEvent event) {
        if (event.clientWorld == null) return;

        if (event.blockEntity instanceof ChestBlockEntity chest) {
            BlockPos pos = chest.getPos();
            Block block = event.clientWorld.getBlockState(pos).getBlock();

            if (block == Blocks.TRAPPED_CHEST) {
                if (!ConfigManager.INSTANCE.getChests().containsKey(pos)) {
                    ConfigManager.INSTANCE.getChests().put(pos, (long) -1);
                }
            }
        }
    }

    // TODO render stuff
    public static void onHudRender(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        boolean hoarder = (getCurrentLootrunData().getCurrentMissionsActive().contains(MissionOptions.Hoarder) && !ScoreboardUtils.missionInProgress.equals("Hoarder"));
        boolean crono = (getCurrentLootrunData().getCurrentMissionsActive().contains(MissionOptions.Chronokinesis) && !ScoreboardUtils.missionInProgress.equals("Chronokinesis"));
        boolean missionReq = ScoreboardUtils.missionChestReq > ScoreboardUtils.missionChestCurrent;
        if (missionReq || crono || hoarder) {
            long currentTime = System.currentTimeMillis();
            long milisIn3Days = 3 * 24 * 60 * 60 * 1000;

            for (Map.Entry<BlockPos, Long> entry : ConfigManager.INSTANCE.getChests().entrySet()) {
                if (ConfigManager.INSTANCE.getBannedChests().contains(entry.getKey())) continue;

                double distSqr = entry.getKey().getSquaredDistance(client.player.getEntityPos());
                // Todo config double maxDist = Math.pow(Config.getMax_Range_To_Show_Chests(), 2);
                // if (distSqr > maxDist) continue;

                if (entry.getValue() == -1 || entry.getValue() + milisIn3Days < currentTime) { // never been opened or 3 days have passed
                    // Todo render WorldRenderUtils.drawEdges()  DrawUtils.drawBlockEdges(context, entry.getKey(), Color.green); // green means has max loot (not opened for 3d)
                    continue;
                }

                if (missionReq || crono) {
                    if (entry.getValue() + 1800000 < currentTime) { // never been opened or 30 minutes has passed
                        // Todo render WorldRenderUtils.drawEdges()  DrawUtils.drawBlockEdges(context, entry.getKey(), Color.getHSBColor(130, 74, 18)); // this literally says s and b should be between 0 and 1 so idk why it gives the right color
                    }
                }
            }
        }
    }

    // runs once TODO better chest tracking
    public static void onScreenOpened(ScreenOpenedEvent event) {
        if (event.client == null || event.client.player == null) return;

        // loot chest highlighter
        if (event.screen instanceof GenericContainerScreen) {
            // this seems horribly unefficient (and unreliable) but yk
            BlockPos closestChest = getClosestChest(event.client);
            if (closestChest != null) {
                if (event.screen.getTitle().getString().startsWith("Loot Chest")) {
                    ConfigManager.INSTANCE.getChests().replace(closestChest, System.currentTimeMillis());
                } else if (event.screen.getTitle().getString().startsWith("Challenge Rewards")) {
                    ConfigManager.INSTANCE.getBannedChests().add(closestChest);
                } else if (event.screen.getTitle().getString().equals("\uDAFF\uDFE8\uE011")){ // player shops
                    ConfigManager.INSTANCE.getBannedChests().add(closestChest);
                }
            }
        }
    }

    private static int completeChaosCounter = Integer.MAX_VALUE;
    public static void onChatMessage(ChatMessageEvent event) {
        if (completeChaosCounter != Integer.MAX_VALUE) completeChaosCounter++;

        if (LootrunningUtils.getCurrentLootrunData().getStatus() == LootrunStatus.PickingBeacon) {
            BeaconOptions.getMatches(event.cleanMessage);
        }

        for (String part : event.splitMessage) {
            if (part.equals("Lootrun Completed")) {
                LootrunningUtils.onChallengeCompleted(LootrunningUtils.getCurrentLootrunData().getActiveBeaconColor());
                LootrunningUtils.getCurrentLootrunData().getEndStats().addEndPulls(1); // assumes you did not fail the last challenge but doesnt really matter so
                // TODO update display
                ChatMessageUtils.sendChatMessage("Lootrun completed with " + LootrunningUtils.getCurrentLootrunData().getEndStats().getEndPulls() + " pulls and " + LootrunningUtils.getEffectivePulls() + " Effective pulls");
                LootrunningUtils.changeStatus(LootrunStatus.NotInLootrun);
            } else if (part.equals("Lootrun Failed")) {
                LootrunningUtils.changeStatus(LootrunStatus.NotInLootrun);
            } else if (part.equals("Choose a Beacon")) {

                // every beacon
                // TODO update display
                LootrunningUtils.getCurrentLootrunData().getCurrentBeaconOptions().clear();
                // TODO update display
                LootrunningUtils.changeStatus(LootrunStatus.PickingBeacon);
                LootrunningUtils.getCurrentLootrunData().setBeaconRerolls(0);

            } else if (part.startsWith("Challenge Failed!")) {
                LootrunningUtils.onChallengeFailed();
            } else if (part.equals("Complete Chaos")) {
                completeChaosCounter = 0;
            }

            if (completeChaosCounter == 2){
                for (BeaconOptions opt : BeaconOptions.values()) {
                    if (part.equals(opt.getDisplayName())) {
                        LootrunningUtils.completeChaosChallengeCompleted(opt);
                    }
                }
            }

            Matcher reRollsMatcher = LootrunningUtils.beaconRerollPattern.matcher(part);
            if (reRollsMatcher.find()) {
                int rerollsLeft = Integer.parseInt(reRollsMatcher.group(1));
                LootrunningUtils.getCurrentLootrunData().setBeaconRerolls(rerollsLeft);
            }

            Matcher endPullsMatcher = LootrunningUtils.endPullsPattern.matcher(part);
            if (endPullsMatcher.find()) {
                int pullsGained = Integer.parseInt(endPullsMatcher.group(1));
                LootrunningUtils.getCurrentLootrunData().getEndStats().addEndPulls(pullsGained);
            }

            Matcher endSacsMatcher = LootrunningUtils.sacrificePattern.matcher(part);
            if (endSacsMatcher.find()) {
                int sacsGained = Integer.parseInt(endSacsMatcher.group(1));
                LootrunningUtils.getCurrentLootrunData().getEndStats().addEndSacs(sacsGained);
            }

            Matcher endRerollsMatcher = LootrunningUtils.endRerollPattern.matcher(part);
            if (endRerollsMatcher.find()) {
                int rrsGained = Integer.parseInt(endRerollsMatcher.group(1));
                LootrunningUtils.getCurrentLootrunData().getEndStats().addEndRerolls(rrsGained);
            }

        }

        Matcher curseMatcher = LootrunningUtils.curseBuffPattern.matcher(event.asciiOnlyMessage);
        if (curseMatcher.find()) {
            int percent = Integer.parseInt(curseMatcher.group(2));
            boolean negative = curseMatcher.group(1).equals("-");
            if (negative) percent *= -1;
            // TODO i think group 3 should be whether its curse or natrual so i can seperate and apply half curse effects if mission
            String type = curseMatcher.group(4);
            switch (type.toLowerCase()) {
                case "health":
                    LootrunningUtils.addMobHealth(percent);
                    return;
                case "resistance":
                    LootrunningUtils.addMobResistance(percent);
                    return;
                case "damage":
                    LootrunningUtils.addMobDamage(percent);
                    return;
                case "attack speed":
                    LootrunningUtils.addMobAttackSpeed(percent);
                    return;
                case "walk speed":
                    LootrunningUtils.addMobSpeed(percent);
                    return;
                default:
            }
        }
    }

    public static void onScreenRender(ScreenRenderEvent event) {
        if (event.screen.getScreenHandler() instanceof GenericContainerScreenHandler chestHandler) {

            ItemStack slot5 = chestHandler.getInventory().getStack(5);
            ItemStack slot4 = chestHandler.getInventory().getStack(4);

            List<Text> loreList = new ArrayList<>();

            LoreComponent lore5 = slot5.getComponents().get(DataComponentTypes.LORE);
            if (lore5 != null) loreList.addAll(lore5.lines());

            LoreComponent lore4 = slot5.getComponents().get(DataComponentTypes.LORE);
            if (lore4 != null) loreList.addAll(lore4.lines());

            for (Text text : loreList) {
                String line = text.getString();
                if (line.contains("Saved Pulls")) {
                    // Extract number using regex
                    Matcher matcher = Pattern.compile("Saved Pulls: §f(\\d+)").matcher(line);
                    if (matcher.find()) {
                        int savedPulls = Integer.parseInt(matcher.group(1));
                        Camps camp = LootrunningUtils.getCurrentLootrunData().getActiveCamp();
                        if (camp == null) {
                            // check if we are close to a camp (prevent wrong camp assignments)
                            MinecraftClient client = MinecraftClient.getInstance();
                            if (client == null || client.player == null || !Camps.isNearAnyCamp(client.player.getEntityPos(), 15)) return;
                            LootrunningUtils.getCurrentLootrunData().setActiveCamp();
                            camp = LootrunningUtils.getCurrentLootrunData().getActiveCamp();
                            if (camp == null) {
                                System.err.println("Camp was null while sacs were on screen");
                                return;
                            }
                        }
                        camp.getCamp().setPossibleSacs(savedPulls);
                    }
                }
            }
        }
    }

}