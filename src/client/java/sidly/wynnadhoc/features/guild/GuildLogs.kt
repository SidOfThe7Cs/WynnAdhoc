package sidly.wynnadhoc.features.guild

import com.wynntils.core.components.Models
import com.wynntils.models.containers.containers.GuildLogContainer
import com.wynntils.models.containers.containers.GuildMemberListContainer
import com.wynntils.models.items.items.gui.GuildLogItem
import com.wynntils.screens.guildlog.GuildLogScreen
import com.wynntils.utils.colors.CommonColors
import com.wynntils.utils.mc.StyledTextUtils
import com.wynntils.utils.render.RenderUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen
import sidly.wynnadhoc.WynnAdhocClient
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.config.gui.HudElementManager
import sidly.wynnadhoc.config.gui.TextHudElement
import sidly.wynnadhoc.event.ChatMessageEvent
import sidly.wynnadhoc.event.ScreenRenderEvent
import sidly.wynnadhoc.mixin.client.accessors.GuildLogScreenAccessor
import sidly.wynnadhoc.mixin.client.accessors.HandledScreenAccessor
import sidly.wynnadhoc.utils.FormatUtils
import sidly.wynnadhoc.utils.PlayerUtils

object GuildLogs {
    private val config get() = ConfigManager.INSTANCE.config.guild

    private var lastSize: Int = 0
    private var lastItem: GuildLogItem? = null

    val RAID_COMPLETED_PATTERN = """(?<p1>[a-zA-Z0-9_]+), (?<p2>[a-zA-Z0-9_]+), (?<p3>[a-zA-Z0-9_]+), and (?<p4>[a-zA-Z0-9_]+) finished (?<raid>.+?) and claimed.*?\+?(?<xp>[0-9.]+)m Guild Experience""".toRegex()
    val ASPECT_REWARDED_PATTERN = """(?<giver>[a-zA-Z0-9_]+) rewarded an Aspect to (?<receiver>[a-zA-Z0-9_]+)""".toRegex()
    val MEMBER_FOR_7DAYS_PATTERN = """(?<p1>[a-zA-Z0-9_]+) needs to be a member for at least 7 days to receive rewards.""".toRegex()

    var raidCompletions = mutableMapOf<String, Int>()
    var givenAspects = mutableMapOf<String, Int>()
    val notEligible = mutableSetOf<String>()

    val aspectsNeeded = mutableMapOf<String, Float>()
    val seenMembers = mutableSetOf<String>()

    fun registerHudElements() {
        HudElementManager.register(TextHudElement(
            config.aspectOverlayData,
            GuildLogs::shouldShowAspectsOverlay,
            GuildLogs::updateAspectsOverlay,
        ))
    }

    fun onScreenRender(event: ScreenRenderEvent) {
        val currentContainer = Models.Container.currentContainer
        if (currentContainer is GuildLogContainer) {
            if (event.screen !is GuildLogScreen) return
            val logs = (event.screen as GuildLogScreenAccessor).holder.guildLogItems

            if (lastItem == null) {
                lastItem = logs.lastOrNull()
                return
            }
            if (lastItem == logs.lastOrNull() || logs.size != 150) return

            val raids = mutableMapOf<String, Int>()
            val aspects = mutableMapOf<String, Int>()

            val logsReversed = logs.reversed()
            for (logEntry in logsReversed) {
                var entry = ""
                for (text in logEntry.logInfo) {
                    val s = StyledTextUtils.unwrap(text).stripAlignment().string
                    entry += FormatUtils.removeNonAscii(s)
                }

                var localFoundRaid = false
                var localFoundAspect = false

                RAID_COMPLETED_PATTERN.find(entry)?.let { regexMatch ->
                    localFoundRaid = true
                    mergeRaids(regexMatch, raids)
                }

                ASPECT_REWARDED_PATTERN.find(entry)?.let { regexMatch ->
                    localFoundAspect = true
                    val p = regexMatch.groups["receiver"]?.value

                    val currentRaidCount = raids.getOrDefault(p, 0)
                    val currentAspectCount = aspects.getOrDefault(p, 0)

                    if (currentAspectCount.toFloat() >= (currentRaidCount.toFloat() / 2f) + 0.5f) {
                        //println("skipping aspect cuz extra: $p raids: $currentRaidCount aspects: $currentAspectCount")
                        continue
                    }// else println("adding aspect: $p raids: $currentRaidCount aspects: $currentAspectCount")

                    if (p != null) aspects.merge(p, 1, Int::plus)
                }

                if (!localFoundRaid && entry.contains("finished")) {
                    WynnAdhocClient.LOGGER.warn("failed to match raid completion entry: $entry")
                }

                if (!localFoundAspect && entry.contains("an Aspect")) {
                    WynnAdhocClient.LOGGER.warn("failed to match aspect given: $entry")
                }
            }

            this.raidCompletions = raids
            this.givenAspects = aspects
            lastSize = logs.size
            lastItem = logs.lastOrNull()
            config.aspectOverlayData.updateDisplay()
        }

        if (currentContainer is GuildMemberListContainer && event.screen is GenericContainerScreen) {
            val bounds = currentContainer.bounds
            val slots = (event.screen as GenericContainerScreen).screenHandler.slots
            val extraSize = 2
            bounds.slots.forEach { slotId ->
                slots[slotId]?.let { slotItem ->
                    val name = FormatUtils.removeColorCodes(slotItem.stack.name.string)
                    if (seenMembers.add(name)) config.aspectOverlayData.updateDisplay()
                    aspectsNeeded[name]?.let { quantity ->
                        var color = if (quantity >= 1) CommonColors.GREEN.withAlpha(0.7f) else CommonColors.YELLOW.withAlpha(0.7f)
                        if (notEligible.contains(name)) color = CommonColors.RED.withAlpha(0.5f)
                        if (quantity > 0) {
                            val accessor = event.screen as HandledScreenAccessor
                            RenderUtils.drawArc(
                                event.context,
                                color,
                                accessor.x + slotItem.x.toFloat() - extraSize,
                                accessor.y + slotItem.y.toFloat() - extraSize,
                                1f,
                                6,
                                8 + extraSize
                            )
                        }
                    }
                }
            }

        }
    }

    fun shouldShowAspectsOverlay(): Boolean {
        val wynntillsLogScreen = MinecraftClient.getInstance().currentScreen is GuildLogScreen
        val vanillaScreen = Models.Container.currentContainer is GuildMemberListContainer
        return wynntillsLogScreen || vanillaScreen
    }

    fun updateAspectsOverlay(): String {
        val sb = StringBuilder()
        updateNeededAspects()

        for (entry in aspectsNeeded) {
            if (entry.value == 0f) continue
            val valColor = if (entry.value <= 0.5) "§c" else "§a"
            val name = PlayerUtils.getRealName(entry.key) ?: entry.key
            val strikethrough = if (notEligible.contains(name)) "§m" else ""
            val nameColor = if (seenMembers.contains(name)) "§7" else "§9"
            sb.append(nameColor).append(strikethrough).append(name)
            sb.append(": ").append(valColor).append(strikethrough)
            sb.append(entry.value).append("\n")
        }

        return sb.toString()
    }

    fun updateNeededAspects() {
        for (entry in raidCompletions) {
            val completions = entry.value
            val given = givenAspects.getOrDefault(entry.key, 0)
            val remaining = (completions.toFloat() * 0.5f) - given.toFloat()
            aspectsNeeded[entry.key] = remaining
        }
    }

    fun onChatMessage(event: ChatMessageEvent) {
        ASPECT_REWARDED_PATTERN.find(event.strippedByWynntills)?.let { regexMatch ->
            val p = regexMatch.groups["receiver"]?.value
            val needed = aspectsNeeded.getOrDefault(p, 0f)
            if (p != null && needed > 0) {
                givenAspects.merge(p, 1, Int::plus)
            }

            config.aspectOverlayData.updateDisplay()
        }

        RAID_COMPLETED_PATTERN.find(event.strippedByWynntills)?.let { regexMatch ->
            mergeRaids(regexMatch, raidCompletions)
        }

        MEMBER_FOR_7DAYS_PATTERN.find(event.strippedByWynntills)?.let { regexMatch ->
            regexMatch.groups["p1"]?.value?.let {
                notEligible.add(it)
                config.aspectOverlayData.updateDisplay()
            }
        }
    }

    private fun mergeRaids(result: MatchResult, map: MutableMap<String, Int>) {
        val p1 = result.groups["p1"]?.value
        val p2 = result.groups["p2"]?.value
        val p3 = result.groups["p3"]?.value
        val p4 = result.groups["p4"]?.value

        if (p1 != null) map.merge(p1, 1, Int::plus)
        if (p2 != null) map.merge(p2, 1, Int::plus)
        if (p3 != null) map.merge(p3, 1, Int::plus)
        if (p4 != null) map.merge(p4, 1, Int::plus)

        config.aspectOverlayData.updateDisplay()
    }
}