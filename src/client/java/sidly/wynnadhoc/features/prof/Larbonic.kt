package sidly.wynnadhoc.features.prof

import net.minecraft.util.math.BlockPos
import sidly.wynnadhoc.WynnAdhocClient
import sidly.wynnadhoc.event.TextDisplaySyncEvent
import sidly.wynnadhoc.utils.Regexs
import sidly.wynnadhoc.utils.text.Fonts
import sidly.wynnadhoc.utils.text.TextPart

object Larbonic {
    private val larbonicLocations = mutableSetOf<BlockPos>()

    fun onTextDisplaySync(event: TextDisplaySyncEvent) {
        checkForLarbonic(event)
        checkMiningProgress(event)
    }

    fun checkMiningProgress(event: TextDisplaySyncEvent) {
        if (!larbonicLocations.contains(event.blockPos)) return
        val index1 = event.parser.findIndex({ part -> part.string == "Mining" })
        val progress = event.parser.get(index1 + 2) ?: return
        if (!(progress.getFontPath()?.equals("nameplate/progress") ?: false)) return
        val numbers = mutableListOf<Int>()
        val font = Fonts.get<Int>("nameplate/progress")
        progress.string.toCharArray().forEach { char ->
            font?.get(char)?.let {
                if (it >= 0) numbers.add(it)
            }
        }
        val percent = numbers.concat()
        WynnAdhocClient.LOGGER.temp("mining progress: $percent")
    }

    fun checkForLarbonic(event: TextDisplaySyncEvent) {
        event.parser.get(0)?.string?.equals("Larbonic Geode") ?: return
        event.parser.find({ part ->
            val result = part.getProfLvlReq()
            val mining = result?.get("type")?.equals("Mining") ?: false
            val lvl = result?.get("lvl")?.toInt() ?: -1
            val valid = mining && (lvl == 105 || lvl == 110)
            if (valid) {
                larbonicLocations.add(event.blockPos)
                WynnAdhocClient.LOGGER.temp("adding larbonic ${event.blockPos}")
            }
            valid
        })
    }
}

fun TextPart.getProfLvlReq(): MatchResult? {
    return Regexs.PROF_LVL_REQ_REGEX.matchEntire(this.string)
}

fun MatchResult.get(group: String): String? {
    return this.groups[group]?.value
}

fun List<Int>.concat(): Int {
    var int = -1
    this.forEachIndexed { index, value ->
        if (index == 0) int = value
        else {
            val multiplier = index * 10
            int += value * multiplier
        }
    }
    return int
}
