package sidly.wynnadhoc.features.lootruns;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Camp {
    private int possibleSacs = 0;
    private int sacs = 0;
    private long lastCompleted = 0;

    public int getSacs() {
        return sacs;
    }

    public long getLastCompleted() {
        return lastCompleted;
    }

    public void justCompleted() {
        lastCompleted = System.currentTimeMillis();
    }

    public boolean isDailyReady() {
        boolean dailyReady;
        if (getLastCompleted() != -1) {
            ZoneId estZone = ZoneId.of("America/New_York");
            ZonedDateTime now = ZonedDateTime.now(estZone);
            ZonedDateTime lastCompletedTime = Instant.ofEpochMilli(getLastCompleted()).atZone(estZone);

            // Determine the most recent reset time (12 PM today or yesterday)
            ZonedDateTime lastReset = now.withHour(23).withMinute(59).withSecond(59).withNano(999);
            if (now.isBefore(lastReset)) {
                // If it's before 11 PM today, reset was at 11 PM yesterday
                lastReset = lastReset.minusDays(1);
            }
            dailyReady = lastCompletedTime.isBefore(lastReset);
        } else dailyReady = true; // never completed
        return dailyReady;
    }

    public void setPossibleSacs(int savedPulls) {
        possibleSacs = savedPulls;
    }

    public void sac() {
        sacs = possibleSacs;
        possibleSacs = 0;
        // update display sac memory
    }

    public void resetSacs() {
        sacs = 0;
        possibleSacs = 0;
        // update display sac memory
    }
}
