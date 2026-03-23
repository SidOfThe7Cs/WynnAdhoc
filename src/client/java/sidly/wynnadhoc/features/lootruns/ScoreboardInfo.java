package sidly.wynnadhoc.features.lootruns;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.event.ClientTickEvent;
import sidly.wynnadhoc.features.lootruns.enums.LootrunStatus;
import sidly.wynnadhoc.features.lootruns.enums.MissionOptions;
import sidly.wynnadhoc.features.lootruns.enums.TrialOptions;
import sidly.wynnadhoc.utils.Debug;
import sidly.wynnadhoc.utils.FormatUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO refactor into not a bunch of individual variables each can have current req and pattern
public class ScoreboardInfo {
    private static boolean shouldPrint = false;
    public static void printScoreboardInfo() {
        shouldPrint = true;
    }

    public static String missionInProgress = "";
    public static String trialInProgress = "";

    public static int currentMaxMissions = -1;
    public static int currentMissionsCompleted = 0;
    public static int timeLeft = -1; // seconds

    public static int missionCurseReq = 0;
    public static int missionCurseCurrent = 0;
    public static int missionChestReq = 0;
    public static int missionChestCurrent = 0;
    public static int missionBoonsReq = 0;
    public static int missionBoonsCurrent = 0;
    public static int missionPullsReq = 0;
    public static int missionPullsCurrent = 0;
    public static int missionBeaconsOfferedReq = 0;
    public static int missionBeaconsOfferedCurrent = 0;
    public static int missionTimeReq = 0;
    public static int missionTimeCurrent = 0;

    public static int splunkChestReq = 0;
    public static int splunkChestCurrent = 0;

    public static String missionLastFrame = "";
    public static String trialLastFrame = "";

    public static boolean inLootrun = false;

    private static final Pattern missionCursePattern = Pattern.compile("- Get (\\d+)/(\\d+) Curses");
    private static final Pattern missionChestPattern = Pattern.compile("- Open (\\d+)/(\\d+) Chests");
    private static final Pattern missionBoonsPattern = Pattern.compile("- Get (\\d+)/(\\d+) Boons");
    private static final Pattern missionPullsPattern = Pattern.compile("- Gain (\\d+)/(\\d+) Pulls");
    private static final Pattern missionBeaconsOfferedPattern = Pattern.compile("- Get offered (\\d+)/(\\d+) Beacons");
    private static final Pattern missionTimePattern = Pattern.compile("- Add (\\d+(?:\\.\\d+)?)/(\\d+)m to your timer");
    private static final Pattern splunkChestPattern = Pattern.compile("Loot (\\d+)/(\\d+) chests!");

    public static void parseScoreboard(ClientTickEvent event){
        if (event.client.world == null) return;
        clearLootrunData(); // TODO if refactor chane everything to have a getCurrent and getLast method
        inLootrun = false;
        List<List<String>> sections = getSections(event.client);

        for (List<String> sect : sections){
            int index = 0;
            int missionIndex = -1;
            int trialIndex = -1;
            if (sect.getFirst().startsWith("Lootrun:")){
                inLootrun = true;
                boolean isMissionInProgress = false;
                boolean isTrialInProgress = false;
                for (String line : sect){
                    // get current splunk progress
                    if (index == 1){
                        if (line.equals("Collect your rewards!")){
                            LootrunCore.INSTANCE.changeStatus(LootrunStatus.ClaimingRewards);
                        }else if(line.equals("Choose a beacon!")){
                            LootrunCore.INSTANCE.changeStatus(LootrunStatus.PickingBeacon);
                        }else if(line.startsWith("Slay!") || line.startsWith("Defend") || line.startsWith("Destroy")){
                            LootrunCore.INSTANCE.changeStatus(LootrunStatus.InChallenge);
                        }


                        Matcher matcher = splunkChestPattern.matcher(line);
                        if (matcher.matches()) {
                            LootrunCore.INSTANCE.changeStatus(LootrunStatus.InChallenge);
                            splunkChestCurrent = Integer.parseInt(matcher.group(1));
                            splunkChestReq = Integer.parseInt(matcher.group(2));
                        }
                    }

                    // get time left
                    Pattern timePattern = Pattern.compile("- Time Left: (\\d+):(\\d+)");
                    Matcher timeMatcher = timePattern.matcher(line);
                    if (timeMatcher.find()) {
                        int minutes = Integer.parseInt(timeMatcher.group(1));
                        int seconds = Integer.parseInt(timeMatcher.group(2));
                        timeLeft = minutes * 60 + seconds;
                    }

                    //get challenge info
                    Pattern challengePattern = Pattern.compile("- Challenges: (\\d+)/(\\d+)");
                    Matcher challengeMatcher = challengePattern.matcher(line);
                    if (challengeMatcher.find()) {
                        int completed = Integer.parseInt(challengeMatcher.group(1));
                        int total = Integer.parseInt(challengeMatcher.group(2));
                        currentMissionsCompleted = completed;
                        currentMaxMissions = total;
                    }

                    @Nullable LootrunData data = LootrunCore.INSTANCE.getCurrentLootrunData();
                    if (data != null) {
                        // get mission info
                        for (MissionOptions mission : MissionOptions.values()) {
                            if (line.startsWith(mission.getDisplayName())) {
                                if (data.getLastCompleted() + 10000 < System.currentTimeMillis()) {
                                    isMissionInProgress = true;
                                    missionInProgress = mission.getDisplayName();
                                    LootrunCore.INSTANCE.addMission(mission.getDisplayName());
                                    missionIndex = index;
                                }
                            }
                        }
                        // get trial info
                        for (TrialOptions trial : TrialOptions.values()) {
                            if (line.startsWith(trial.getDisplayName())) {
                                if (data.getLastCompleted() + 10000 < System.currentTimeMillis()) {
                                    isTrialInProgress = true;
                                    trialInProgress = trial.getDisplayName();
                                    LootrunCore.INSTANCE.addTrial(trial.getDisplayName());
                                    trialIndex = index;
                                }
                            }
                        }
                        index++;
                    }
                }

                // check if mission has ended
                if (!isMissionInProgress && !missionLastFrame.isEmpty()) {
                    LootrunCore.INSTANCE.onMissionCompleted(missionLastFrame);
                    missionInProgress = "";
                    ConfigManager.INSTANCE.config.lootrun.missionOverlay.updateDisplay();
                }
                if (!isTrialInProgress && !trialLastFrame.isEmpty()) {
                    LootrunCore.INSTANCE.onTrialCompleted(trialLastFrame);
                    trialInProgress = "";
                    ConfigManager.INSTANCE.config.lootrun.missionOverlay.updateDisplay();
                }

                index = -1;
                for (String line : sect){
                    index++;
                    if (index <= missionIndex && index <= trialIndex) continue;

                    Matcher matcher = missionCursePattern.matcher(line);
                    if (matcher.matches()) {
                        missionCurseCurrent = Integer.parseInt(matcher.group(1));
                        missionCurseReq = Integer.parseInt(matcher.group(2));
                    }

                    matcher = missionChestPattern.matcher(line);
                    if (matcher.matches()) {
                        missionChestCurrent = Integer.parseInt(matcher.group(1));
                        missionChestReq = Integer.parseInt(matcher.group(2));
                    }

                    matcher = missionBoonsPattern.matcher(line);
                    if (matcher.matches()) {
                        missionBoonsCurrent = Integer.parseInt(matcher.group(1));
                        missionBoonsReq = Integer.parseInt(matcher.group(2));
                    }

                    matcher = missionPullsPattern.matcher(line);
                    if (matcher.matches()) {
                        missionPullsCurrent = Integer.parseInt(matcher.group(1));
                        missionPullsReq = Integer.parseInt(matcher.group(2));
                    }

                    matcher = missionBeaconsOfferedPattern.matcher(line);
                    if (matcher.matches()) {
                        missionBeaconsOfferedCurrent = Integer.parseInt(matcher.group(1));
                        missionBeaconsOfferedReq = Integer.parseInt(matcher.group(2));
                    }

                    matcher = missionTimePattern.matcher(line);
                    if (matcher.matches()) {
                        double timeCur = Double.parseDouble(matcher.group(1));
                        missionTimeCurrent = (int)(timeCur * 60);
                        missionTimeReq = Integer.parseInt(matcher.group(2)) * 60;
                    }
                }
                missionLastFrame = missionInProgress;
                trialLastFrame = trialInProgress;
            }

            // add handling for other sections
        }


        if (shouldPrint) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            sb.append("SCOREBOARD:\n");
            for (List<String> sect : sections){
                for (String line : sect){
                    sb.append(line);
                }
                sb.append("SECTION SEPARATOR\n");
            }

            sb.append("PARSED INFO:\n");
            sb.append("currentMaxMissions: ").append(currentMaxMissions).append("\n");
            sb.append("currentMissionsCompleted: ").append(currentMissionsCompleted).append("\n");
            sb.append("timeLeft (seconds): ").append(timeLeft).append("\n");
            sb.append("missionCurseReq: ").append(missionCurseReq).append("\n");
            sb.append("missionCurseCurrent: ").append(missionCurseCurrent).append("\n");
            sb.append("missionChestReq: ").append(missionChestReq).append("\n");
            sb.append("missionChestCurrent: ").append(missionChestCurrent).append("\n");
            sb.append("missionBoonsReq: ").append(missionBoonsReq).append("\n");
            sb.append("missionBoonsCurrent: ").append(missionBoonsCurrent).append("\n");
            sb.append("missionPullsReq: ").append(missionPullsReq).append("\n");
            sb.append("missionPullsCurrent: ").append(missionPullsCurrent).append("\n");
            sb.append("missionBeaconsOfferedReq: ").append(missionBeaconsOfferedReq).append("\n");
            sb.append("missionBeaconsOfferedCurrent: ").append(missionBeaconsOfferedCurrent).append("\n");
            sb.append("missionTimeReq: ").append(missionTimeReq).append("\n");
            sb.append("missionTimeCurrent: ").append(missionTimeCurrent).append("\n");
            sb.append("splunkChestReq: ").append(splunkChestReq).append("\n");
            sb.append("splunkChestCurrent: ").append(splunkChestCurrent).append("\n");
            sb.append("mission in progress: ").append(missionInProgress).append("\n");
            @Nullable LootrunData data = LootrunCore.INSTANCE.getCurrentLootrunData();

            if (data != null) {
                for (MissionOptions mission : data.getCurrentMissionsActive()) {
                    sb.append("Mission: ").append(mission).append("\n");
                }
                sb.append("trial in progress: ").append(trialInProgress).append("\n");
                for (TrialOptions trial : data.getCurrentTrialsActive()) {
                    sb.append("Trial: ").append(trial).append("\n");
                }
            }

            WynnAdhocClient.LOGGER.info(Debug.Type.MANUAL, sb.toString());
            shouldPrint = false;
        }
    }

    private static @NotNull List<List<String>> getSections(@NotNull MinecraftClient client) {
        ClientWorld world = client.world;
        if  (world == null) return Collections.emptyList();
        Scoreboard scoreboard = world.getScoreboard();
        Collection<ScoreboardObjective> objectives = scoreboard.getObjectives();

        if (objectives.isEmpty()) return Collections.emptyList();

        // Get the first objective only
        ScoreboardObjective firstObjective = objectives.iterator().next();
        if (firstObjective.getName().equals("wynntilsSB")) return Collections.emptyList();

        List<ScoreboardEntry> sortedEntries = new ArrayList<>(scoreboard.getScoreboardEntries(firstObjective));
        sortedEntries.sort(Comparator.comparingInt(ScoreboardEntry::value).reversed());

        List<List<String>> sections = new ArrayList<>();
        List<String> currentSection = new ArrayList<>();

        for (ScoreboardEntry entry : sortedEntries) {
            String name = FormatUtils.removeColorCodes(entry.name().getString());

            if (name.matches("À+")) {
                // Only add the current section if it's not empty
                if (!currentSection.isEmpty()) {
                    sections.add(currentSection);
                    currentSection = new ArrayList<>();
                }
            } else {
                currentSection.add(name);
            }
        }

        if (!currentSection.isEmpty()) {
            sections.add(currentSection);
        }
        return sections;
    }

    public static void clearLootrunData(){
        missionInProgress = "";
        trialInProgress = "";
        currentMaxMissions = -1;
        currentMissionsCompleted = 0;
        timeLeft = -1; // seconds

        missionCurseReq = 0;
        missionCurseCurrent = 0;
        missionChestReq = 0;
        missionChestCurrent = 0;
        missionBoonsReq = 0;
        missionBoonsCurrent = 0;
        missionPullsReq = 0;
        missionPullsCurrent = 0;
        missionBeaconsOfferedReq = 0;
        missionBeaconsOfferedCurrent = 0;
        missionTimeReq = 0;
        missionTimeCurrent = 0;

        splunkChestReq = 0;
        splunkChestCurrent = 0;
    }
}
