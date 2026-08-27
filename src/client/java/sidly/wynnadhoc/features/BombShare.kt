package sidly.wynnadhoc.features

import com.mojang.brigadier.context.CommandContext
import com.wynntils.core.components.Models
import com.wynntils.core.text.StyledText
import com.wynntils.core.text.StyledTextPart
import com.wynntils.core.text.type.StyleType
import com.wynntils.models.worlds.type.BombInfo
import com.wynntils.models.worlds.type.BombType
import com.wynntils.utils.type.IterationDecision
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.event.ChatMessageEvent
import sidly.wynnadhoc.event.CommandRegistrationEvent
import sidly.wynnadhoc.utils.ChatMessageUtils
import sidly.wynnadhoc.utils.ClickEventFunction
import java.util.*
import java.util.function.Consumer
import java.util.regex.Pattern
import java.util.stream.Collectors
import kotlin.math.max

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
        if (!ConfigManager.INSTANCE.config.toggles.bombShare) return
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
                ?.sortedBy { it.second }
                ?.reversed()
                ?.joinToString(", ") { it.first }
                ?: "NONE"
        } else {
            val profXp: MutableList<Pair<String, Long>> = activeBombs[BombType.PROFESSION_XP] ?: mutableListOf()
            val profSpeed: MutableList<Pair<String, Long>> = activeBombs[BombType.PROFESSION_SPEED] ?: mutableListOf()
            val xpMap = profXp.associate { it.first to it.second }

            val profXpServers = profXp.map { it.first }.toSet()
            val (bothServers, speedOnlyServers) = profSpeed
                .sortedBy { max(it.second, xpMap.get(it.first) ?: 0L) }
                .reversed()
                .map { it.first }
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

    enum class Bomb(val type: BombType?, val displayName: String, val clickEvent: ClickEventFunction) {
        PROF(null, "Profs", ClickEventFunction.BOMB_SHARE_PROF),
        LOOT(BombType.LOOT, "Loot", ClickEventFunction.BOMB_SHARE_LOOT),
        CHEST_LOOT(BombType.LOOT_CHEST, "Chest Loot", ClickEventFunction.BOMB_SHARE_CHEST),
        XP(BombType.COMBAT_XP, "DXp", ClickEventFunction.BOMB_SHARE_XP);

        fun getClickEvent(): ClickEvent {
            return clickEvent.clickEvent
        }

        fun getChatCommand(): Runnable {
            return Runnable {
                ChatMessageUtils.sendChatCommand(
                    "g $displayName on ${
                        getBombShare(
                            this
                        )
                    }"
                )
            }
        }
    }

    fun registerCommands(event: CommandRegistrationEvent) {
        event.register(
            ClientCommandManager.literal("ShareBombs")
                .then(
                    ClientCommandManager.literal("prof")
                        .executes { _: CommandContext<FabricClientCommandSource> ->
                            Bomb.PROF.getChatCommand().run()
                            1
                        }
                )
                .then(
                    ClientCommandManager.literal("xp")
                        .executes { _: CommandContext<FabricClientCommandSource> ->
                            Bomb.XP.getChatCommand().run()
                            1
                        }
                )
                .then(
                    ClientCommandManager.literal("loot")
                        .executes { _: CommandContext<FabricClientCommandSource> ->
                            Bomb.LOOT.getChatCommand().run()
                            1
                        }
                )
                .then(
                    ClientCommandManager.literal("chest")
                        .executes { _: CommandContext<FabricClientCommandSource> ->
                            Bomb.CHEST_LOOT.getChatCommand().run()
                            1
                        }
                )
        )
    }
}
