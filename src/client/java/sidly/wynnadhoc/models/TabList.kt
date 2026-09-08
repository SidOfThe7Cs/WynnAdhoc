package sidly.wynnadhoc.models

import net.minecraft.client.MinecraftClient
import sidly.wynnadhoc.WynnAdhocClient
import sidly.wynnadhoc.event.ClientTickEvent
import sidly.wynnadhoc.features.prof.get
import sidly.wynnadhoc.utils.Regexs
import sidly.wynnadhoc.utils.datatypes.Server
import sidly.wynnadhoc.utils.text.DefaultTextParser

object TabList {
    private val data = mutableMapOf<EntryType, TabListEntry>()
    var currentServer: Server? = null

    fun onTick(event: ClientTickEvent) {
        //WynnAdhocClient.LOGGER.temp("footer " + playerListHud.footer?.string)
        //WynnAdhocClient.LOGGER.temp("header " + playerListHud.header?.string)
        data.clear()
        val client = MinecraftClient.getInstance()
        var currentColumn: EntryType? = null
        val playerListHud = client.inGameHud.playerListHud
        if (client.player != null) playerListHud.collectPlayerEntries().forEach { entry ->
            entry?.displayName?.let { displayName ->
                val parser = DefaultTextParser(displayName)
                if (parser.get().size > 1) {
                    WynnAdhocClient.LOGGER.warn("tablist multiline: $displayName")
                }
                val part = parser.get(0) ?: return@forEach

                if (part.string == "§a  §lFriends") {
                    currentColumn = EntryType.FRIEND
                    return@forEach
                }
                if (part.string == "§e  §lParty") {
                    currentColumn = EntryType.PARTY
                    return@forEach
                }
                if (part.string == "§f  §lHousing Island") {
                    currentColumn = EntryType.HOUSING
                    return@forEach
                }
                if (part.string == "§d  §lIsland Info") {
                    currentColumn = EntryType.HOUSING
                    return@forEach
                }
                if (part.string == "§b§l  Guild") {
                    currentColumn = EntryType.GUILD
                    return@forEach
                }
                Regexs.TAB_GLOBAL_HEADER_REGEX.matchEntire(part.string)?.let {
                    currentColumn = EntryType.GLOBAL
                    currentServer = Server.fromMatch(it)
                    return@forEach
                }

                if (part.string == "§7Make a party") return@forEach
                if (part.string == "§7by typing:") return@forEach
                if (part.string == "§7/party create") return@forEach

                currentColumn ?: return@forEach
                if (currentColumn == EntryType.HOUSING) return@forEach

                Regexs.TAB_LINE_PLAYER_REGEX.matchEntire(part.string)?.let { match ->
                    val username = match.get("username") ?: return@forEach
                    val server = Server.fromMatch(match)
                    val serverColor = match.get("serverColor")
                    val nameColor = match.get("nameColor")

                    // handles all global entries
                    if (nameColor == null && serverColor == null) {
                        if (currentColumn != EntryType.GLOBAL) {
                            WynnAdhocClient.LOGGER.warn("invalid entry found in global tab: $displayName")
                            return@forEach
                        }
                        data[currentColumn] = TabListEntry(username, server, false, part.getColorRgb())
                        return@forEach
                    }

                    // handles online friends
                    if ((serverColor == "e" || serverColor == "2") && nameColor == "a") {
                        if (currentColumn != EntryType.FRIEND) {
                            WynnAdhocClient.LOGGER.warn("friend found in $currentColumn column: $displayName")
                            return@forEach
                        }
                        data[currentColumn] = TabListEntry(username, server, false, null)
                        return@forEach
                    }

                    // handles online guild members
                    if ((serverColor == "e" || serverColor == "3") && nameColor == "b") {
                        if (currentColumn != EntryType.GUILD) {
                            WynnAdhocClient.LOGGER.warn("guild member found in $currentColumn column: $displayName")
                            return@forEach
                        }
                        data[currentColumn] = TabListEntry(username, server, false, null)
                        return@forEach
                    }
                }

                Regexs.COLORED_USERNAME_REGEX.matchEntire(part.string)?.let { match ->
                    val username = match.get("username") ?: return@forEach
                    val color = match.get("color")

                    // handles all offline entries
                    if (color == "7") {
                        data[currentColumn] = TabListEntry(username, null, false, null)
                        return@forEach
                    }

                    if (currentColumn == EntryType.PARTY) {
                        val partyLeader = color == "c"
                        if (!partyLeader && color != "e") {
                            WynnAdhocClient.LOGGER.warn("invalid entry found in party tab: $displayName")
                            return@forEach
                        }
                        data[currentColumn] = TabListEntry(username, null, partyLeader, null)
                        return@forEach
                    }
                }

                //WynnAdhocClient.LOGGER.warn("unrecognized tab entry found in $currentColumn column: $part")
            }
        }
    }

    data class TabListEntry(val name: String, val server: Server?, val partyLeader: Boolean?, val rankColor: Int?)

    enum class EntryType {
        FRIEND,
        GLOBAL,
        PARTY,
        GUILD,
        HOUSING,
    }
}