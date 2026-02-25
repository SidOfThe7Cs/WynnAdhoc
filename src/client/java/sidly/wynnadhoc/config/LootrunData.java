package sidly.wynnadhoc.config;

import sidly.wynnadhoc.features.lootruns.BeaconCounters;
import sidly.wynnadhoc.features.lootruns.Camp;
import sidly.wynnadhoc.features.lootruns.EndStats;
import sidly.wynnadhoc.features.lootruns.enums.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LootrunData {
    private final BeaconCounters beaconCounts = new BeaconCounters();
    private final List<BeaconOptions> currentBeaconOptions = new ArrayList<>();
    private final List<MissionOptions> currentMissionsActive = new ArrayList<>();
    private final List<TrialOptions> currentTrialsActive = new ArrayList<>();
    private final EndStats endStats = new EndStats();
    private LootrunStatus status = LootrunStatus.NotInLootrun;
    private AquaStatus aquaStatus = AquaStatus.Inactive;
    private BeaconColor activeBeaconColor = null;
    private BeaconColor possibleActiveBeaconColor = null;
    private Camps activeCamp = null;
    private int beaconRerolls = 0;
    private final Map<Camps, Camp> campData;
    private final long lastCompleted; // actually time since object creation, but we create a new object as soon as we finish a lr (only used for fixing a delay in scoreboard changes)
    private int pullsSinceLastYellow = 0;

    public int getPullsSinceLastYellow() {
        return pullsSinceLastYellow;
    }

    public void addPullsSinceLastYellow(int value) {
        pullsSinceLastYellow += value;
    }

    public void resetPullsSinceLastYellow() {
        pullsSinceLastYellow = 0;
    }

    public Map<Camps, Camp> getCampData() {
        return campData;
    }

    public long getLastCompleted() {
        return lastCompleted;
    }

    public LootrunData(Map<Camps, Camp> campData) {
        this.campData = campData;
        lastCompleted = System.currentTimeMillis();
    }

    public void startLootrun() {
        setActiveCamp();
    }

    public void setActiveCamp() {
        activeCamp = Camps.getClosestCamp();
    }

    public Camps getActiveCamp() {
        return activeCamp;
    }

    public BeaconCounters getBeaconCounts() {
        return beaconCounts;
    }

    public LootrunStatus getStatus() {
        return status;
    }

    public AquaStatus getAquaStatus() {
        return aquaStatus;
    }

    public void setStatus(LootrunStatus status) {
        this.status = status;
    }

    public void setAquaStatus(AquaStatus aquaStatus) {
        this.aquaStatus = aquaStatus;
    }

    public List<BeaconOptions> getCurrentBeaconOptions() {
        return currentBeaconOptions;
    }

    public List<MissionOptions> getCurrentMissionsActive() {
        return currentMissionsActive;
    }

    public List<TrialOptions> getCurrentTrialsActive() {
        return currentTrialsActive;
    }

    public BeaconColor getActiveBeaconColor() {
        return activeBeaconColor;
    }

    public BeaconColor getPossibleActiveBeaconColor() {
        return possibleActiveBeaconColor;
    }

    public void clearActiveBeaconColor() {
        this.activeBeaconColor = null;
    }

    public void activateBeacon() {
        activeBeaconColor = possibleActiveBeaconColor;
        possibleActiveBeaconColor = null;
    }

    public void setPossibleActiveBeaconColor(BeaconColor possibleActiveBeaconColor) {
        this.possibleActiveBeaconColor = possibleActiveBeaconColor;
    }

    public EndStats getEndStats() {
        return endStats;
    }

    public int getBeaconRerolls() {
        return beaconRerolls;
    }

    public void setBeaconRerolls(int beaconRerolls) {
        this.beaconRerolls = beaconRerolls;
        // TODO update display fix beacon reroll tracking
    }
}
