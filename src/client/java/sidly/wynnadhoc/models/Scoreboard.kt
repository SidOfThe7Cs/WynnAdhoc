package sidly.wynnadhoc.models

import net.minecraft.client.MinecraftClient
import net.minecraft.scoreboard.ScoreboardEntry
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import sidly.wynnadhoc.features.prof.get
import sidly.wynnadhoc.utils.Regexs
import java.util.function.ToIntFunction

object Scoreboard {
    // TODO call parse every tick and post event (ScoreboardSectionEvent(header: Header, lines: MutableList<Text>))

    private val nameMap =
        mutableMapOf<String, String>() // TODO also update from self() name and from party join chat messages

    fun getPartyMembers(): MutableList<PartyMember> {
        val fullNames = TabList.EntryType.PARTY.getData()?.map { it.name }?.toList()

        // all sections are guaranteed to not be empty
        val members = mutableListOf<PartyMember>()
        getSections().forEach { section ->
            Regexs.SCOREBOARD_PARTY_HEADER.matchEntire(section[0].string)?.let {
                section.forEach { line ->
                    val string = Formatting.strip(line.string) ?: return@forEach
                    Regexs.SCOREBOARD_PARTY_MEMBER.matchEntire(string)?.let { match ->
                        val health = match.get("health")?.toInt() ?: -1
                        val name = match.get("username") ?: return@forEach
                        val lvl = match.get("lvl")?.toInt() ?: -1
                        val fullName = nameMap[name]
                            ?: fullNames?.find { fullName -> fullName.startsWith(name) }?.also { nameMap[name] = it }
                            ?: name
                        members.add(PartyMember(fullName, health, lvl))
                    }
                }
            }
        }
        return members
    }

    private fun getSections(): MutableList<MutableList<Text>> {
        val scoreboard = MinecraftClient.getInstance().world?.scoreboard ?: return mutableListOf()
        val objectives = scoreboard.objectives

        if (objectives.isEmpty()) return mutableListOf()

        // Get the first objective only
        val firstObjective = objectives.iterator().next()
        if (firstObjective.name == "wynntilsSB") return mutableListOf()

        val sortedEntries: MutableList<ScoreboardEntry> = ArrayList(scoreboard.getScoreboardEntries(firstObjective))
        sortedEntries.sortWith(
            Comparator.comparingInt(ToIntFunction { obj: ScoreboardEntry -> obj.value() }).reversed()
        )

        val sections: MutableList<MutableList<Text>> = ArrayList()
        var currentSection: MutableList<Text> = ArrayList()

        for (entry in sortedEntries) {
            val name = entry.name().string

            if (name.matches("À+".toRegex())) {
                // Only add the current section if it's not empty
                if (!currentSection.isEmpty()) {
                    sections.add(currentSection)
                    currentSection = ArrayList()
                }
            } else {
                currentSection.add(entry.name())
            }
        }

        if (!currentSection.isEmpty()) {
            sections.add(currentSection)
        }
        return sections
    }

    data class PartyMember(val name: String, val hp: Int, val lvl: Int)
}