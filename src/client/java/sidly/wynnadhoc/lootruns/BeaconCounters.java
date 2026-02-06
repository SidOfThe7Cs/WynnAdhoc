package sidly.wynnadhoc.lootruns;

import java.util.*;

public class BeaconCounters {
    private Map<BeaconColor, Integer> counters = new HashMap<>();
    private List<Integer> orangeBeaconChallengesLeft;
    private int redBeaconChallengesLeft;
    private int rainbowBeaconChallengesLeft;

    public String getChallengesRemaining(BeaconColor color) {
        return switch (color){
            case Red -> String.valueOf(redBeaconChallengesLeft);
            case Rainbow -> String.valueOf(rainbowBeaconChallengesLeft);
            case Orange -> {
                StringBuilder sb = new StringBuilder();
                if (!orangeBeaconChallengesLeft.isEmpty()) {
                    List<Integer> sorted = new ArrayList<>(orangeBeaconChallengesLeft);
                    sorted.sort(Comparator.reverseOrder()); // Sort in descending order

                    for (Integer remaining : sorted) {
                        if (remaining > 0) {
                            sb.append(" (").append(remaining).append(")");
                        }else break;
                    }
                }
                yield sb.toString();
            }
            default -> throw new IllegalStateException("Unexpected value: " + color);
        };
    }

    public void addRemaining(BeaconColor color, int value) {
        switch (color) {
            case Red -> redBeaconChallengesLeft += value;
            case Rainbow -> rainbowBeaconChallengesLeft += value;
            case Orange -> orangeBeaconChallengesLeft.add(value);
        }
    }

    public void decreaseRemaining() {
        if (rainbowBeaconChallengesLeft > 0) rainbowBeaconChallengesLeft--;
        if (redBeaconChallengesLeft > 0) redBeaconChallengesLeft--;
        orangeBeaconChallengesLeft.replaceAll(value -> value > 0 ? value - 1 : value);
    }

    public int getCount(BeaconColor color) {
        return counters.get(color);
    }
    public void incrementCount(BeaconColor color) {
        counters.merge(color, 1, Integer::sum);
    }

    public BeaconCounters() {
        for (BeaconColor color : BeaconColor.values()) {
            counters.put(color, 0);
        }
        this.orangeBeaconChallengesLeft = new ArrayList<>();
        this.redBeaconChallengesLeft = 0;
        this.rainbowBeaconChallengesLeft = 0;
    }

    public void ensureDefaults() {
        if (counters == null) {
            counters = new HashMap<>();
        }
        for (BeaconColor color : BeaconColor.values()) {
            counters.putIfAbsent(color, 0);
        }
        if (orangeBeaconChallengesLeft == null) orangeBeaconChallengesLeft = new ArrayList<>();
    }

    public void reset() {
        for (BeaconColor color : BeaconColor.values()) {
            counters.put(color, 0);
        }
        this.orangeBeaconChallengesLeft = new ArrayList<>();
        this.redBeaconChallengesLeft = 0;
        this.rainbowBeaconChallengesLeft = 0;
    }
}
