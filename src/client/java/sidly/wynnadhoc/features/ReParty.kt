package sidly.wynnadhoc.features

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import sidly.wynnadhoc.event.ChatMessageEvent
import sidly.wynnadhoc.event.CommandRegistrationEvent
import sidly.wynnadhoc.utils.ChatMessageUtils
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object ReParty {
    private var waitingForPartyList = false
    private var newServer = ""
    private val savedPartyMembers: MutableList<String> = ArrayList()
    private val PARTY_LIST: Pattern = Pattern.compile("^Party members: (.+)$")

    fun registerCommands(event: CommandRegistrationEvent) {
        event.dispatcher.register(
            ClientCommandManager.literal("rp")
                .executes { _: CommandContext<FabricClientCommandSource> ->
                    inviteSavedMembers()
                    1
                }
                .then(
                    ClientCommandManager.literal("save")
                        .executes { _: CommandContext<FabricClientCommandSource> ->
                            waitingForPartyList = true
                            ChatMessageUtils.sendChatCommand("party list")
                            1
                        }
                )
                .then(
                    ClientCommandManager.argument("switch_to", StringArgumentType.string())
                        .executes { ctx: CommandContext<FabricClientCommandSource> ->
                            waitingForPartyList = true
                            newServer = ctx.getArgument("switch_to", String::class.java)
                            ChatMessageUtils.sendChatCommand("party list")
                            1
                        }
                )
        )

        event.register(
            ClientCommandManager.literal("reParty")
                .then(
                    ClientCommandManager.literal("save")
                        .executes { _: CommandContext<FabricClientCommandSource> ->
                            waitingForPartyList = true
                            ChatMessageUtils.sendChatCommand("party list")
                            1
                        }
                )
                .then(
                    ClientCommandManager.literal("invite")
                        .executes { _: CommandContext<FabricClientCommandSource> ->
                            inviteSavedMembers()
                            1
                        }
                )
        )
    }

    private fun inviteSavedMembers() {
        if (savedPartyMembers.isEmpty()) {
            ChatMessageUtils.sendChatMessage("No saved party cannot invite")
        }
        ChatMessageUtils.sendChatCommand("party create")

        val playerName = MinecraftClient.getInstance().player!!.name.string
        val scheduler = Executors.newScheduledThreadPool(1)

        var delay: Long = 0
        for (member in savedPartyMembers) {
            if (member == playerName) continue  // skip self

            delay += 300L // add delay before to wait for party create cmd
            scheduler.schedule(Runnable {
                MinecraftClient.getInstance()
                    .execute(Runnable { ChatMessageUtils.sendChatCommand("party invite $member") })
            }, delay, TimeUnit.MILLISECONDS)
        }
        // all cmds are scheduled immediately with increasing delay and shutdown waits for all currently scheduled to finish
        scheduler.shutdown()
    }

    fun getPartyMembers(msg: String): MutableList<String> {
        val matcher = PARTY_LIST.matcher(msg)
        if (!matcher.matches()) {
            return ArrayList()
        }

        var members = matcher.group(1)
        members = members.replace(" and ", " ")
        val parts = members.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val result: MutableList<String> = ArrayList()

        for (part in parts) {
            result.add(part.trim { it <= ' ' })
        }

        return result
    }

    fun onChat(event: ChatMessageEvent) {
        if (!waitingForPartyList) return
        val msg = event.strippedByWynntills

        if (msg == "You must be in a party to use this.") {
            waitingForPartyList = false
            newServer = ""
            return
        }

        val partyMembers = getPartyMembers(msg)
        if (!partyMembers.isEmpty()) {
            waitingForPartyList = false
            event.canceled = true
            savedPartyMembers.clear()
            savedPartyMembers.addAll(partyMembers)

            val sb = StringBuilder("Saved " + savedPartyMembers.size + " party members as:\n")
            for (member in savedPartyMembers) {
                sb.append(member).append("\n")
            }
            ChatMessageUtils.sendChatMessage(sb.toString())
            if (!newServer.isEmpty()) {
                ChatMessageUtils.sendChatCommand("switch $newServer")
                newServer = ""
            }
            //TODO on world swap reinv
        }
    }
}
