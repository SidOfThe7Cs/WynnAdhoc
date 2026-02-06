package sidly.wynnadhoc.lootruns;

import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import sidly.wynnadhoc.event.ClientTickEvent;
import sidly.wynnadhoc.utils.FormatUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreboardUtils {
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

    public static void parseScoreboard(ClientTickEvent event){
        if (event.client.world == null) return;

        clearLootrunData();
        inLootrun = false;

        MinecraftClient client = event.client;
        Scoreboard scoreboard = client.world.getScoreboard();
        Collection<ScoreboardObjective> objectives = scoreboard.getObjectives();

        if (objectives.isEmpty()) return;

        // Get the first objective only
        ScoreboardObjective firstObjective = objectives.iterator().next();
        if (firstObjective.getName().equals("wynntilsSB")) return;

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

        Pattern missionCursePattern = Pattern.compile("- Get (\\d+)/(\\d+) Curses"); // works
        Pattern missionChestPattern = Pattern.compile("- Open (\\d+)/(\\d+) Chests"); // works
        Pattern missionBoonsPattern = Pattern.compile("- Get (\\d+)/(\\d+) Boons"); // works
        Pattern missionPullsPattern = Pattern.compile("- Gain (\\d+)/(\\d+) Pulls"); // works
        Pattern missionBeaconsOfferedPattern = Pattern.compile("- Get offered (\\d+)/(\\d+) Beacons"); // works
        Pattern missionTimePattern = Pattern.compile("- Add (\\d+(?:\\.\\d+)?)/(\\d+)m to your timer"); // works

        Pattern splunkChestPattern = Pattern.compile("Loot (\\d+)/(\\d+) chests!"); // works

        for (List<String> sect : sections){
            int index = 0;
            int missionIndex = -1;
            int trialIndex = -1;
            if (sect.getFirst().startsWith("Lootrun:")){
                inLootrun = true;
                boolean isMissionInProgress = false;
                boolean isTrialInProgress = false;
                for (String line : sect){
                    // get current splenk progress
                    if (index == 1){
                        if (line.equals("Collect your rewards!")){
                            LootrunningUtils.changeStatus(LootrunStatus.ClaimingRewards);
                        }else if(line.equals("Choose a beacon!")){
                            LootrunningUtils.changeStatus(LootrunStatus.PickingBeacon);
                        }else if(line.startsWith("Slay!") || line.startsWith("Defend") || line.startsWith("Destroy")){
                            LootrunningUtils.changeStatus(LootrunStatus.InChallenge);
                        }


                        Matcher matcher = splunkChestPattern.matcher(line);
                        if (matcher.matches()) {
                            LootrunningUtils.changeStatus(LootrunStatus.InChallenge);
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

                    // get mission info
                    for (MissionOptions mission : MissionOptions.values()) {
                        if (line.startsWith(mission.getDisplayName())) {
                            if (LootrunningUtils.getCurrentLootrunData().getLastCompleted() + 10000 < System.currentTimeMillis()) {
                                isMissionInProgress = true;
                                missionInProgress = mission.getDisplayName();
                                LootrunningUtils.addMission(mission.getDisplayName());
                                missionIndex = index;
                            }
                        }
                    }
                    // get trial info
                    for (TrialOptions trial : TrialOptions.values()) {
                        if (line.startsWith(trial.getDisplayName())) {
                            if (LootrunningUtils.getCurrentLootrunData().getLastCompleted() + 10000 < System.currentTimeMillis()) {
                                isTrialInProgress = true;
                                trialInProgress = trial.getDisplayName();
                                LootrunningUtils.addTrial(trial.getDisplayName());
                                trialIndex = index;
                            }
                        }
                    }
                    index++;
                }

                // check if mission has ended
                //System.out.println(!isMissionInProgress + " & " + !missionInProgress.isEmpty());
                if (!isMissionInProgress && !missionLastFrame.isEmpty()) {
                    LootrunningUtils.onMissionCompleted(missionLastFrame);
                    missionInProgress = "";
                    // TODO update display
                }
                if (!isTrialInProgress && !trialLastFrame.isEmpty()) {
                    LootrunningUtils.onTrialCompleted(trialLastFrame);
                    trialInProgress = "";
                    // TODO update display
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
            System.out.println("SCOREBOARD:");
            for (List<String> sect : sections){
                for (String line : sect){
                    System.out.println(line);
                }
                System.out.println("SECTION SEOPERATOR");
            }

            System.out.println("PARSED INFO:");

            System.out.println("currentMaxMissions: " + currentMaxMissions);
            System.out.println("currentMissionsCompleted: " + currentMissionsCompleted);
            System.out.println("timeLeft (seconds): " + timeLeft);

            System.out.println("missionCurseReq: " + missionCurseReq);
            System.out.println("missionCurseCurrent: " + missionCurseCurrent);
            System.out.println("missionChestReq: " + missionChestReq);
            System.out.println("missionChestCurrent: " + missionChestCurrent);
            System.out.println("missionBoonsReq: " + missionBoonsReq);
            System.out.println("missionBoonsCurrent: " + missionBoonsCurrent);
            System.out.println("missionPullsReq: " + missionPullsReq);
            System.out.println("missionPullsCurrent: " + missionPullsCurrent);
            System.out.println("missionBeaconsOfferedReq: " + missionBeaconsOfferedReq);
            System.out.println("missionBeaconsOfferedCurrent: " + missionBeaconsOfferedCurrent);
            System.out.println("missionTimeReq: " + missionTimeReq);
            System.out.println("missionTimeCurrent: " + missionTimeCurrent);

            System.out.println("splunkChestReq: " + splunkChestReq);
            System.out.println("splunkChestCurrent: " + splunkChestCurrent);

            System.out.println();
            System.out.println("mission in progeress: " + missionInProgress);
            for (MissionOptions mission : LootrunningUtils.getCurrentLootrunData().getCurrentMissionsActive()){
                System.out.println("Mission: " + mission);
            }
            System.out.println("trial in progeress: " + trialInProgress);
            for (TrialOptions trial : LootrunningUtils.getCurrentLootrunData().getCurrentTrialsActive()){
                System.out.println("Trial: " + trial);
            }
            shouldPrint = false;
        }
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
