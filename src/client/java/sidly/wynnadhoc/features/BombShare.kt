package sidly.wynnadhoc.features

import com.wynntils.core.components.Models
import com.wynntils.core.text.StyledText
import com.wynntils.core.text.StyledTextPart
import com.wynntils.core.text.type.StyleType
import com.wynntils.models.worlds.type.BombInfo
import com.wynntils.models.worlds.type.BombType
import com.wynntils.utils.type.IterationDecision
import net.minecraft.text.ClickEvent
import net.minecraft.text.ClickEvent.RunCommand
import net.minecraft.text.Style
import net.minecraft.util.Pair
import sidly.wynnadhoc.event.ChatMessageEvent
import java.util.*
import java.util.function.Consumer
import java.util.regex.Pattern
import java.util.stream.Collectors

object BombShare {
    private val prof = mutableSetOf("prof", "profs", "speed bomb", "gxp")
    private val xp = mutableSetOf("xp bomb", "dxp", "cxp", "txp", "double xp")
    private val chest = mutableSetOf("loot chest", "dcl", "chest loot", "cloot")
    private val loot = mutableSetOf("dloot", "loot bomb")
    private val clickEventsToAdd: MutableMap<String, Bomb> = mutableMapOf()

    init {
        prof.forEach(Consumer { s: String ->
            clickEventsToAdd[s] = Bomb.PROF
        })
        xp.forEach(Consumer { s: String ->
            clickEventsToAdd[s] = Bomb.XP
        })
        chest.forEach(Consumer { s: String ->
            clickEventsToAdd[s] = Bomb.CHEST_LOOT
        })
        loot.forEach(Consumer { s: String ->
            clickEventsToAdd[s] = Bomb.LOOT
        })
    }

    fun onChat(event: ChatMessageEvent) {
        event.styledText = addClickEvents(event.styledText, clickEventsToAdd)
    }

    fun getBombShare(bomb: Bomb): String {
        val activeBombs: MutableMap<BombType, MutableList<Pair<String, Long>>> = EnumMap(BombType::class.java)
        Models.Bomb.bombBells.forEach(Consumer { b: BombInfo ->
            val type = b.bomb()
            val activeList = activeBombs.computeIfAbsent(type) { _: BombType -> mutableListOf() }
            activeList.add(Pair<String, Long>(b.server(), b.remainingLong))
        })
        if (bomb != Bomb.PROF) {
            return activeBombs[bomb.type]
                ?.sortedBy { it.right }
                ?.reversed()
                ?.joinToString(", ") { it.left }
                ?: "NONE"
        } else {
            val profXp: MutableList<Pair<String, Long>> = activeBombs[BombType.PROFESSION_XP] ?: mutableListOf()
            val profSpeed: MutableList<Pair<String, Long>> = activeBombs[BombType.PROFESSION_SPEED] ?: mutableListOf()

            val profXpServers = profXp.map { it.left }.toSet()
            val (bothServers, speedOnlyServers) = profSpeed
                .sortedBy { it.right }
                .reversed()
                .map { it.left }
                .partition { it in profXpServers }

            val both = bothServers.joinToString(", ").ifEmpty { "NONE" }
            val speedOnly = speedOnlyServers.joinToString(", ")

            if (speedOnly.isEmpty()) {
                return both
            } else return "$both Only Speed on $speedOnly"
        }
    }

    fun addClickEvents(
        styledText: StyledText,
        wordClickMap: MutableMap<String, Bomb>
    ): StyledText {
        if (wordClickMap.isEmpty()) {
            return styledText
        }

        // Build case-insensitive pattern
        val patternString = wordClickMap.keys.stream()
            .sorted { a: String, b: String -> b.length.compareTo(a.length) }
            .map { s: String -> Pattern.quote(s) }
            .collect(Collectors.joining("|"))

        val pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE)

        return styledText.iterate { part: StyledTextPart, functionParts: MutableList<StyledTextPart> ->
            val partText = part.getString(null, StyleType.NONE)
            val matcher = pattern.matcher(partText)

            val newParts: MutableList<StyledTextPart> = ArrayList<StyledTextPart>()
            var lastEnd = 0
            var foundMatch = false

            while (matcher.find()) {
                foundMatch = true
                val matchedWord = matcher.group()
                // Find the original word in the map (case-insensitive)
                val originalWord = wordClickMap.keys.stream()
                    .filter { word: String -> word.equals(matchedWord, ignoreCase = true) }
                    .findFirst()
                    .orElse(matchedWord)

                val clickEvent = wordClickMap.getOrDefault(originalWord, null)?.getClickEvent() ?: continue

                if (matcher.start() > lastEnd) {
                    val beforeText = partText.substring(lastEnd, matcher.start())
                    newParts.add(
                        StyledTextPart(
                            beforeText,
                            part.partStyle.style,
                            null,
                            Style.EMPTY
                        )
                    )
                }

                // Use the actual matched text (preserving case) but with click event
                val originalStyle = part.partStyle.style
                val newStyle = originalStyle.withClickEvent(clickEvent)
                newParts.add(
                    StyledTextPart(
                        matchedWord,  // Use matched text to preserve original case
                        newStyle,
                        null,
                        Style.EMPTY
                    )
                )

                lastEnd = matcher.end()
            }

            if (foundMatch) {
                if (lastEnd < partText.length) {
                    val afterText = partText.substring(lastEnd)
                    newParts.add(
                        StyledTextPart(
                            afterText,
                            part.partStyle.style,
                            null,
                            Style.EMPTY
                        )
                    )
                }

                functionParts.clear()
                functionParts.addAll(newParts)
            }
            IterationDecision.CONTINUE
        }
    }

    enum class Bomb(val type: BombType?, val displayName: String) {
        PROF(null, "Profs"),
        LOOT(BombType.LOOT, "Loot"),
        CHEST_LOOT(BombType.LOOT_CHEST, "Chest Loot"),
        XP(BombType.COMBAT_XP, "DXp");

        fun getClickEvent(): ClickEvent {
            return RunCommand("g $displayName on ${getBombShare(this)}")
        }
    }
}
