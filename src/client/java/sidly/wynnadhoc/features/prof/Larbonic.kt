package sidly.wynnadhoc.features.prof

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.util.math.BlockPos
import sidly.wynnadhoc.WynnAdhocClient
import sidly.wynnadhoc.event.CommandRegistrationEvent
import sidly.wynnadhoc.event.TextDisplaySyncEvent
import sidly.wynnadhoc.features.c2c.C2CConnectionManager
import sidly.wynnadhoc.features.c2c.packets.LarbonicJoinRequestC2CPacket
import sidly.wynnadhoc.features.c2c.packets.LarbonicPartyMemberUpdateC2CPacket
import sidly.wynnadhoc.models.Party
import sidly.wynnadhoc.utils.PlayerUtils
import sidly.wynnadhoc.utils.Regexs
import sidly.wynnadhoc.utils.text.Fonts
import sidly.wynnadhoc.utils.text.TextPart

//TODO
// this all needs to be tested
// nicked players will break it (by it i mean all the party code)
// add online/offline to party stuff
// track party members and send join requests to new members and disband party if you leave etc
// actually implement the mining progress packet

object Larbonic {
    private val larbonicLocations = mutableSetOf<BlockPos>()

    private var isInLarbonicParty = false
    private val larbonicPartyMembers = mutableSetOf<String>()
    private val sentJoinPackets = mutableSetOf<String>()

    fun addMember(newMember: String = "") {
        isInLarbonicParty = true
        if (newMember.isNotEmpty()) larbonicPartyMembers.add(newMember)
        if (PlayerUtils.self().isNotEmpty()) larbonicPartyMembers.add(PlayerUtils.self())

        val updatePacket = LarbonicPartyMemberUpdateC2CPacket(PlayerUtils.self(), larbonicPartyMembers)
        val partyMembers = Party.getPartyMembers()
        partyMembers.forEach { partyMember ->
            if (partyMember == PlayerUtils.self()) return@forEach

            if (sentJoinPackets.contains(partyMember)) {
                C2CConnectionManager.queuePacket(updatePacket, partyMember)
                return@forEach
            }

            if (partyMember == newMember) return@forEach
            val joinRequestPacket = LarbonicJoinRequestC2CPacket(PlayerUtils.self())
            C2CConnectionManager.queuePacket(joinRequestPacket, partyMember)
            sentJoinPackets.add(partyMember)
        }
    }

    fun setPartyMembers(newMembers: Set<String>) {
        larbonicPartyMembers.clear()
        larbonicPartyMembers.addAll(newMembers)
    }

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
            }
            valid
        })
    }

    fun register(event: CommandRegistrationEvent) {
        event.register(
            ClientCommandManager.literal("c2c")
                .then(
                    ClientCommandManager.literal("startLarbonic")
                        .executes { ctx: CommandContext<FabricClientCommandSource> ->
                            WynnAdhocClient.LOGGER.temp("starting larbonic party")
                            1
                        }
                )
        )
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
