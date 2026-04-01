package sidly.wynnadhoc.features.lootruns;

import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.LootrunConfig;
import sidly.wynnadhoc.config.gui.HudElementManager;
import sidly.wynnadhoc.config.gui.TextHudComponent;
import sidly.wynnadhoc.features.lootruns.enums.BeaconColor;
import sidly.wynnadhoc.features.lootruns.enums.MissionOptions;
import sidly.wynnadhoc.features.lootruns.enums.TrialOptions;
import sidly.wynnadhoc.utils.FormatUtils;

public class Overlays {
    private static LootrunConfig config() { return ConfigManager.INSTANCE.config.lootrun; }
    private static LootrunData data = LootrunCore.INSTANCE.getCurrentLootrunData();

    public static void register() {
        HudElementManager.register(new TextHudComponent(
                config().beaconCountsOverlay,
                Overlays::shouldShowBeaconCountsOverlay,
                Overlays::updateBeaconCountsOverlay)
        );

        HudElementManager.register(new TextHudComponent(
                config().endRewardsOverlay,
                Overlays::shouldShowEndRewardsOverlay,
                Overlays::updateEndRewardsOverlay)
        );

        HudElementManager.register(new TextHudComponent(
                config().missionOverlay,
                Overlays::shouldShowMissionOverlay,
                Overlays::updateMissionOverlay)
        );
    }

    public static Boolean shouldShowBeaconCountsOverlay() {
        if (ScoreboardInfo.inLootrun) {
            return config().showBeaconCountsOverlay;
        }
        return false;
    }

    public static String updateBeaconCountsOverlay() {
        data = LootrunCore.INSTANCE.getCurrentLootrunData();
        StringBuilder sb = new StringBuilder();
        for (BeaconColor color : BeaconColor.values()) {
            addBeaconCountLine(sb, color);
        }
        return sb.toString();
    }
    private static void addBeaconCountLine(StringBuilder sb, BeaconColor color) {
        int num = data.getBeaconCounts().getCount(color);
        String left = data.getBeaconCounts().getChallengesRemaining(color);

        if (color.getMax() != -1 && num >= color.getMax()) sb.append("§m");
        sb.append(color.getColorCode());
        sb.append(color.getDisplayName()).append(": ");
        sb.append(num);
        if (!left.isEmpty()) {
            if (color != BeaconColor.Orange) sb.append(" (");
            sb.append(left);
            if (color != BeaconColor.Orange) sb.append(")");
        }
        sb.append('\n');
    }

    public static Boolean shouldShowEndRewardsOverlay() {
        if (ScoreboardInfo.inLootrun) {
            return config().showEndRewardsOverlay;
        }
        return false;
    }

    public static String updateEndRewardsOverlay() {
        data = LootrunCore.INSTANCE.getCurrentLootrunData();
        if (data == null) return "lootrun data is null";
        int rrs = data.getEndStats().getEndRerolls();
        int pulls = data.getEndStats().getEndPulls();
        String timeTillReset = "";
        int prevSacs = 0;
        StringBuilder sb = new StringBuilder();

        if (data.getActiveCamp() != null) {
            if (data.getActiveCamp().getCamp().isDailyReady()) {
                rrs += 1;
                pulls += 10;
            } else timeTillReset = " (" + FormatUtils.millisToHMS(LootrunCore.INSTANCE.getTimeTillDailyReset()) + ")";
            prevSacs = data.getActiveCamp().getCamp().getSacs();
        } else sb.append("camp is null cant get sac data\n");

        sb.append("pulls: ").append(pulls).append(" (").append(prevSacs + pulls).append(")").append('\n')
                .append("sacs: ").append(data.getEndStats().getEndSacs()).append('\n')
                .append("rerolls: ").append(rrs).append(timeTillReset).append('\n')
                .append("epulls: ").append(LootrunCore.INSTANCE.getEffectivePulls()).append('\n');

        return sb.toString();
    }

    public static Boolean shouldShowMissionOverlay() {
        if (ScoreboardInfo.inLootrun) {
            return config().showMissionOverlay;
        }
        return false;
    }

    public static String updateMissionOverlay() {
        data = LootrunCore.INSTANCE.getCurrentLootrunData();
        if (data == null) return "lootrun data is null";
        StringBuilder sb = new StringBuilder();
        for (MissionOptions mission : data.getCurrentMissionsActive()){
            sb.append("§7");
            if (ScoreboardInfo.missionInProgress.equals(mission.getDisplayName())){
                // if theres a active mission
                sb.append("§m");
            }
            sb.append(mission.getDisplayName());

            if (mission == MissionOptions.InterestScheme) {
                sb.append(" ").append(data.getPullsSinceLastYellow()).append("/24");
            }

            sb.append('\n');
        }
        for (TrialOptions trial : data.getCurrentTrialsActive()){
            sb.append("§4");
            if (ScoreboardInfo.trialInProgress.equals(trial.getDisplayName())){
                sb.append("§m");
            }
            sb.append(trial.getDisplayName());
            sb.append('\n');
        }
        return sb.toString();
    }
}
