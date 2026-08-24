package sidly.wynnadhoc.utils

import sidly.wynnadhoc.WynnAdhocClient
import sidly.wynnadhoc.event.ClientTickEvent

object DelayedRun {
    var currentTicks: Long = 0

    private val queue: MutableMap<Long, MutableSet<Runnable>> = mutableMapOf()
    private val lock = Any()

    fun onTick(event: ClientTickEvent) {
        val tasks = synchronized(lock) {
            queue.remove(currentTicks)
        }
        tasks?.forEach { it.run() }
        currentTicks++
    }

    fun runDelayed(runnable: Runnable, futureTicks: Int) {
        if (futureTicks < 0) {
            WynnAdhocClient.LOGGER.warn("You may not schedule tasks to run in the past")
        } else if (futureTicks == 0) {
            runnable.run()
        } else {
            val targetTick = currentTicks + futureTicks
            synchronized(lock) {
                queue.computeIfAbsent(targetTick) { mutableSetOf() }.add(runnable)
            }
        }
    }
}
