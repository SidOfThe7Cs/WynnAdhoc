package sidly.wynnadhoc.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class TickScheduler {
    private static final List<TickScheduler> registeredSchedulers = new ArrayList<>();

    private final List<ScheduledTask> tasks = new ArrayList<>();
    private boolean cancelPending = false;

    private TickScheduler() {
    } // private constructor forces to use the getter

    /**
     * Register this scheduler in the static registry.
     * Returns the scheduler for convenience (allows chaining).
     */
    public static TickScheduler getNewScheduler() {
        TickScheduler scheduler = new TickScheduler();
        if (!registeredSchedulers.contains(scheduler)) {
            registeredSchedulers.add(scheduler);
        }
        return scheduler;
    }

    /**
     * Tick all registered schedulers
     */
    public static void tickAll() {
        for (TickScheduler scheduler : registeredSchedulers) {
            scheduler.tick();
        }
    }

    /**
     * Schedule a task
     *
     * @param delayTicks   Delay between executions (in ticks)
     * @param repeatCount  How many times to repeat
     * @param task         The task, receives remaining repeats
     * @param runInstantly If true, first run happens immediately
     */
    public void schedule(int delayTicks, int repeatCount, Consumer<Integer> task, boolean runInstantly) {
        tasks.add(new ScheduledTask(delayTicks, repeatCount, task, runInstantly));
    }

    /**
     * Call this once per tick to update all scheduled tasks
     */
    public void tick() {
        if (cancelPending) {
            tasks.clear();
            cancelPending = false;
            return;
        }

        Iterator<ScheduledTask> iter = tasks.iterator();
        while (iter.hasNext()) {
            ScheduledTask t = iter.next();
            t.ticksLeft--;
            if (t.ticksLeft <= 0) {
                t.task.accept(t.repeatCountLeft); // pass remaining repeats
                if (t.repeatCountLeft > 1) {
                    t.repeatCountLeft--;
                    t.ticksLeft = t.initialDelay; // reset delay
                } else {
                    iter.remove();
                }
            }
        }
    }

    /**
     * Cancel all tasks in this scheduler instance
     */
    public void cancelAll() {
        cancelPending = true; // safely cleared at start of next tick
    }

    /**
     * Inner class representing a scheduled task
     */
    private static class ScheduledTask {
        int ticksLeft;
        final int initialDelay;
        int repeatCountLeft;
        final Consumer<Integer> task;

        ScheduledTask(int delayTicks, int repeatCount, Consumer<Integer> task, boolean runInstantly) {
            this.ticksLeft = runInstantly ? 0 : delayTicks;
            this.initialDelay = delayTicks;
            this.repeatCountLeft = repeatCount;
            this.task = task;
        }
    }
}

