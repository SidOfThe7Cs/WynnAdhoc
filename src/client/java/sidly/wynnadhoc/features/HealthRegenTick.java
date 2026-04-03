package sidly.wynnadhoc.features;

import com.wynntils.core.components.Models;
import com.wynntils.core.consumers.functions.Function;
import com.wynntils.core.consumers.functions.arguments.FunctionArguments;
import com.wynntils.utils.type.CappedValue;
import sidly.wynnadhoc.utils.datatypes.CircularBuffer;

import java.util.List;
import java.util.Optional;

public class HealthRegenTick {
    private static final int VALID_COUNT = 3;
    private static final int TARGET_TICKS = 79;

    private static int lastHealth = -1;
    private static final CircularBuffer<Integer> recentHealths = new CircularBuffer<>(250);
    private static long ticksSinceLastConfirmedHprTick = -1;

    public static double getProjectedSeconds() {
        return (TARGET_TICKS - (ticksSinceLastConfirmedHprTick % TARGET_TICKS)) / 20.0;
    }

    public static class NextHealthRegenTickFunction extends Function<Double> {
        @Override
        public Double getValue(FunctionArguments arguments) {
            return getProjectedSeconds();
        }

        @Override
        protected List<String> getAliases() {
            return List.of("next_hpr_tick");
        }
    }

    public static void onTick() {
        if (ticksSinceLastConfirmedHprTick != -1) ticksSinceLastConfirmedHprTick++;
        Optional<CappedValue> healthOpt = Models.CharacterStats.getHealth();
        if (healthOpt.isEmpty()) {
            return;
        }
        int health = healthOpt.get().current();
        recentHealths.insert(health);

        if (lastHealth != -1 && lastHealth != health) {
            if (isValid()) {
                ticksSinceLastConfirmedHprTick = 0;
            }
        }

        lastHealth = health;
    }

    private static boolean isValid() {
        if (!recentHealths.isFull()) return false;
        int lastValue = -1;
        int validCounter = 0;
        for (int b = 0; b < VALID_COUNT; b++) {
            int base = b * TARGET_TICKS;
            for (int i = base + TARGET_TICKS - 3; i <= base + TARGET_TICKS + 3; i++) {
                if (lastValue != -1 && recentHealths.get(i) != lastValue) {
                    validCounter++;
                    if (validCounter >= VALID_COUNT) return true;
                    break;
                }
                lastValue = recentHealths.get(i);
            }
            lastValue = -1;
        }
        return false;
    }

    public static void onWorldChange() {
        recentHealths.reset();
        ticksSinceLastConfirmedHprTick = -1;
    }

}
