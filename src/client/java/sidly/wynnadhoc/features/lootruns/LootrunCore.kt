package sidly.wynnadhoc.features.lootruns

import com.wynntils.core.components.Models
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity
import net.minecraft.item.Item.TooltipContext
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text
import sidly.wynnadhoc.WynnAdhocClient
import sidly.wynnadhoc.config.ConfigManager
import sidly.wynnadhoc.config.catagories.LootrunConfig
import sidly.wynnadhoc.event.*
import sidly.wynnadhoc.features.lootruns.enums.*
import sidly.wynnadhoc.mixin.client.accessors.WynntillsEventBusAccessor
import sidly.wynnadhoc.utils.ChatMessageUtils
import sidly.wynnadhoc.utils.Debug
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import java.util.regex.Pattern

object LootrunCore {
    private fun config(): LootrunConfig {
        return ConfigManager.INSTANCE.config.lootrun
    }

    fun getCurrentLootrunData(): LootrunData? {
        try {
            if (WynntillsEventBusAccessor.getEventBus() == null) return null
            val uuid = Models.Character.id
            return ConfigManager.INSTANCE.getLootrun(uuid)
        } catch (_: Exception) {
            return null
        }
    }

    // this is the actual text displays rendered in the world cleared every tick
    var currentBeaconOptionsFromWaypoints: MutableMap<BeaconColor, Int> = mutableMapOf()
    var beaconOptionsLastTick = false

    // i just don't care right now (or ever) its both broken and useless
    var mobHealthIncrease: Int = 0
    var mobResistanceIncrease: Int = 0
    var mobDamageIncrease: Int = 0
    var mobAttackSpeedIncrease: Int = 0
    var mobSpeedIncrease: Int = 0
    fun addMobHealth(amount: Int) {
        mobHealthIncrease += amount
        // update display
    }

    fun addMobResistance(amount: Int) {
        mobResistanceIncrease += amount
        // update display
    }

    fun addMobDamage(amount: Int) {
        mobDamageIncrease += amount
        // update display
    }

    fun addMobAttackSpeed(amount: Int) {
        mobAttackSpeedIncrease += amount
        // update display
    }

    fun addMobSpeed(amount: Int) {
        mobSpeedIncrease += amount
        // update display
    }

    var curseBuffPattern: Pattern = Pattern.compile("\\[([+-])(\\d+)% (Mob|Enemy) (.+?)]")
    var beaconRerollPattern: Pattern = Pattern.compile("\\((\\d+) rerolls left\\)")
    var endPullsPattern: Pattern = Pattern.compile("\\[\\+?(\\d+) Reward Pulls?]")
    var sacrificePattern: Pattern = Pattern.compile("\\[\\+(\\d+) Reward Sacrifice]")
    var endRerollPattern: Pattern = Pattern.compile("\\[\\+(\\d+) End Reward Reroll]")


    fun checkIfBeacon(event: ForEachEntityEvent) {
        // we can also add type detection by checking the last character in the display name
        if (event.entity is TextDisplayEntity) {
            val rootText: Text = event.entity.getText()
            val siblings = rootText.siblings

            // check if the first sibling is a "marker"
            if (!siblings.isEmpty() && siblings[0].style.getFont() != null && siblings[0].style
                    .getFont().toString() == "Font[id=minecraft:marker]"
            ) {
                // extract the distance
                var distance = -1
                if (siblings.size >= 3) {
                    val distanceText = siblings[2]
                    val distanceStr =
                        distanceText.string.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                    distance = distanceStr.replace("[^0-9]".toRegex(), "").toInt()
                }

                // get the color
                val markerSibling = siblings[0]
                for (nested in markerSibling.siblings) {
                    if (nested.style.getColor() != null) {
                        val color = nested.style.getColor()!!.rgb

                        val baseColor: BeaconColor
                        when (color) {
                            0x5C5CE6 -> baseColor = BeaconColor.Blue
                            0xFF00FF -> baseColor = BeaconColor.Purple
                            0xFFFF33 -> baseColor = BeaconColor.Yellow
                            0x55FFFF -> baseColor = BeaconColor.Aqua
                            0xff9500 -> baseColor = BeaconColor.Orange
                            0xff80 -> baseColor = BeaconColor.Green
                            0x808080 -> baseColor = BeaconColor.DarkGrey
                            0xffffff -> baseColor = BeaconColor.White
                            0xbfbfbf -> baseColor = BeaconColor.Grey
                            0xff0000 -> baseColor = BeaconColor.Red
                            0xf000 -> baseColor = BeaconColor.Rainbow
                            0xf010 -> baseColor = BeaconColor.Crimson
                            else -> {
                                WynnAdhocClient.LOGGER.warn("unrecognized beacon color: " + Integer.toHexString(color) + " distance: " + distance)
                                return
                            }
                        }
                        //WynnAdhocClient.LOGGER.info(Debug.Type.TEMP, "$baseColor beacon found at $distance")
                        if (!currentBeaconOptionsFromWaypoints.containsKey(baseColor)) {
                            currentBeaconOptionsFromWaypoints[baseColor] = distance
                        }
                    }
                }

                // get beacon type
                // Get the nested components within the marker
                val markerComponents = markerSibling.siblings
                if (markerComponents.size > 1) {
                    // The symbol is in the second nested component (index 1)
                    val symbolComponent = markerComponents[1]
                    val symbolText = symbolComponent.string

                    val lastSymbol =
                        symbolText.codePoints().skip((symbolText.codePointCount(0, symbolText.length) - 1).toLong())
                            .findFirst().orElse(-1)
                    val unicodeString = "U+" + String.format("%04X", lastSymbol)
                    var type = ""
                    when (unicodeString) {
                        "U+E00B" -> type = "Slay"
                        "U+E00C" -> type = "Target"
                        "U+E00D" -> type = "Defend"
                        "U+E00E" -> type = "Loot"
                        "U+E00F" -> type = "Destroy"
                        else -> WynnAdhocClient.LOGGER.warn("Unknown beacon type, Last symbol: $unicodeString Distance: $distance\n")
                    }
                }
            }
        }
    }

    // adds a mission to the active list but active list also contains in progress so called from scoreboard
    fun addMission(name: String) {
        val data = getCurrentLootrunData() ?: return
        for (opt in MissionOptions.entries) {
            if (name.contains(opt.displayName)) {
                val alreadyExists = data.currentMissionsActive.stream()
                    .anyMatch { existing: MissionOptions -> existing.displayName == opt.displayName }

                if (!alreadyExists) {
                    data.currentMissionsActive.add(opt)
                    LootrunLogger.appendLine("Mission started: ${opt.displayName}")
                }

                config().missionOverlay.updateDisplay()
            }
        }
    }

    fun addTrial(name: String) {
        val data = getCurrentLootrunData() ?: return
        for (opt in TrialOptions.entries) {
            if (name.contains(opt.displayName)) {
                val alreadyExists = data.currentTrialsActive.stream()
                    .anyMatch { existing: TrialOptions -> existing.displayName == opt.displayName }

                if (!alreadyExists) {
                    data.currentTrialsActive.add(opt)
                    LootrunLogger.appendLine("started trial: ${opt.displayName}")
                }

                config().missionOverlay.updateDisplay()
            }
        }
    }

    // TODO dont think this works + it should reduce the cap of limited beacons if you fail a limited
    fun onChallengeFailed() {
        val data = getCurrentLootrunData() ?: return

        LootrunLogger.appendLine("failed challenge")

        data.clearActiveBeaconColor()
        data.aquaStatus = AquaStatus.Inactive
        data.beaconCounts.decreaseRemaining()

        config().beaconCountsOverlay.updateDisplay()
    }

    fun onChallengeCompleted(color: BeaconColor?) {
        val data = getCurrentLootrunData() ?: return
        if (color == null) WynnAdhocClient.LOGGER.warn("completed null beacon color: $color")

        // check the available beacon options to see if its vibrant or not
        var vibrant = false
        var beaconCompleted = ""
        for (beacon in data.currentBeaconOptions) {
            if (beacon.baseColor == color) {
                beaconCompleted = beacon.displayName
                break
            }
        }
        if (beaconCompleted.startsWith("Vibrant")) vibrant = true

        val message = "completed " + (if (vibrant) "vibrant " else "") + color + " beacon"
        WynnAdhocClient.LOGGER.info(Debug.Type.LOOTRUN, message)
        LootrunLogger.appendLine(message)

        data.beaconCounts.decreaseRemaining()
        data.beaconCounts.incrementCount(color)
        when (color) {
            BeaconColor.Yellow -> data.resetPullsSinceLastYellow()
            BeaconColor.Purple -> {
                val phobia = (data.currentMissionsActive.contains(MissionOptions.Porphyrophobia)
                        && ScoreboardInfo.missionInProgress != "Porphyrophobia")
                val pulls = getBeaconMultiplier(1, vibrant)
                data.endStats.addEndPulls(if (phobia) pulls * 2 else pulls)
            }

            BeaconColor.Aqua -> if (vibrant) {
                data.aquaStatus = AquaStatus.Vibrant
            } else data.aquaStatus = AquaStatus.Active

            BeaconColor.Orange -> data.beaconCounts
                .addRemaining(BeaconColor.Orange, getBeaconMultiplier(5, vibrant))

            BeaconColor.DarkGrey -> data.endStats.addEndPulls(getBeaconMultiplier(3, vibrant))
            BeaconColor.Red -> {
                var redToAdd = 3
                if (vibrant) redToAdd = 5
                if (data.aquaStatus == AquaStatus.Vibrant) redToAdd *= 3
                else if (data.aquaStatus == AquaStatus.Active) redToAdd *= 2
                data.beaconCounts.addRemaining(BeaconColor.Red, redToAdd)
            }

            BeaconColor.Rainbow -> {
                val rainbowToAdd = getBeaconMultiplier(10, vibrant)
                data.beaconCounts.addRemaining(BeaconColor.Rainbow, rainbowToAdd)
            }

            else -> {}
        }

        if (color != BeaconColor.Aqua) data.aquaStatus = AquaStatus.Inactive

        data.clearActiveBeaconColor()

        config().beaconCountsOverlay.updateDisplay()
    }

    fun completeChaosChallengeCompleted(beacon: BeaconOptions) {
        val message = "complete chaos gave " + beacon.displayName
        WynnAdhocClient.LOGGER.info(Debug.Type.LOOTRUN, message)
        LootrunLogger.appendLine(message)
        val data = getCurrentLootrunData() ?: return

        val vibrant = beacon.displayName.startsWith("Vibrant")
        var multiplier = 1
        if (vibrant) multiplier = 2

        val color = beacon.baseColor

        when (color) {
            BeaconColor.Purple -> {
                val phobia = (data.currentMissionsActive.contains(MissionOptions.Porphyrophobia)
                        && ScoreboardInfo.missionInProgress != "Porphyrophobia")
                data.endStats.addEndPulls(if (phobia) multiplier * 2 else multiplier)
            }

            BeaconColor.Aqua -> if (vibrant) {
                data.aquaStatus = AquaStatus.Vibrant
            } else data.aquaStatus = AquaStatus.Active

            BeaconColor.Orange -> data.beaconCounts.addRemaining(BeaconColor.Orange, 5 * multiplier)
            BeaconColor.DarkGrey -> data.endStats.addEndPulls(3 * multiplier)
            BeaconColor.Red -> {
                var redToAdd = 3
                if (vibrant) redToAdd = 5
                data.beaconCounts.addRemaining(BeaconColor.Red, redToAdd)
            }

            BeaconColor.Rainbow -> data.beaconCounts.addRemaining(BeaconColor.Rainbow, 10 * multiplier)
            else -> {}
        }
    }

    private fun getBeaconMultiplier(baseValue: Int, vibrant: Boolean): Int {
        var multiplier = 1
        if (vibrant) multiplier *= 2
        getCurrentLootrunData()?.let {
            if (it.aquaStatus == AquaStatus.Vibrant) multiplier *= 3
            else if (it.aquaStatus == AquaStatus.Active) multiplier *= 2
        }
        return baseValue * multiplier
    }

    fun onMissionCompleted(mission: String) {
        val message = "Mission completed: $mission"
        WynnAdhocClient.LOGGER.info(Debug.Type.LOOTRUN, message)
        LootrunLogger.appendLine(message)
        val data = getCurrentLootrunData() ?: return
        when (mission) {
            "High Roller" -> {
                data.endStats.addEndRerolls(1)
                return
            }

            "Inner Peace" ->                 // curses half effective
                return

            "Redemption" -> {
                data.endStats.addEndSacs(1)
                return
            }

            "Interest Scheme" -> {
                data.resetPullsSinceLastYellow()
                return
            }
        }
    }

    fun onTrialCompleted(trial: String) {
        val message = "Trial completed: $trial"
        WynnAdhocClient.LOGGER.info(Debug.Type.LOOTRUN, message)
        LootrunLogger.appendLine(message)
        val data = getCurrentLootrunData() ?: return
        when (trial) {
            "All In" -> {
                // in reality this happens at the end of lootrun not instantly
                data.endStats
                    .addEndRerolls(data.endStats.endSacs * 2)
                data.endStats.clearEndSacs()
                data.endStats.addEndSacs(0)
                return
            }

            "Hubris", "Warmth Devourer" -> {
                data.endStats.addEndRerolls(1)
                data.endStats.addEndSacs(1)
                return
            }

            "Side Hustle" -> {
                data.endStats.addEndRerolls(2)
                return
            }

            "Treasury Bill" -> {
                data.endStats
                    .addEndPulls((data.endStats.endPulls * 0.7).toInt())
                return
            }

            "Ultimate Sacrifice" -> {
                data.endStats.addEndSacs(2)
                return
            }
        }
    }

    fun changeStatus(newStatus: LootrunStatus) {
        val data = getCurrentLootrunData() ?: return
        val oldStatus = data.status
        if (oldStatus == newStatus) return
        WynnAdhocClient.LOGGER.info(Debug.Type.LOOTRUN, "switching lootrun status from $oldStatus to $newStatus")
        when (newStatus) {
            LootrunStatus.PickingBeacon -> if (oldStatus == LootrunStatus.NotInLootrun) {
                data.startLootrun()
                config().endRewardsOverlay.updateDisplay()
                config().beaconCountsOverlay.updateDisplay()
                config().missionOverlay.updateDisplay()
            }

            LootrunStatus.InChallenge -> if (oldStatus == LootrunStatus.PickingBeacon) {
                data.activateBeacon()
            }

            LootrunStatus.ClaimingRewards -> if (oldStatus == LootrunStatus.InChallenge) {
                onChallengeCompleted(data.activeBeaconColor)
            }

            LootrunStatus.NotInLootrun -> {
                endLootrun()
            }
        }
        data.status = newStatus
    }

    fun getEffectivePulls(): Int {
        val data = getCurrentLootrunData() ?: return -1
        var epulls = 0
        if (data.activeCamp != null) {
            var rrs = data.endStats.endRerolls
            var pulls = data.endStats.endPulls

            if (data.activeCamp.camp.isDailyReady()) {
                rrs += 1
                pulls += 10
            }

            epulls += pulls
            epulls += data.activeCamp.camp.sacs
            epulls *= (rrs + 1)
        }
        return epulls
    }

    fun getTimeTillDailyReset(): Long {
        val estZone = ZoneId.of("America/New_York")
        val now = ZonedDateTime.now(estZone)
        var todayAt12pm = now.withHour(23).withMinute(59).withSecond(59).withNano(999)

        // If it's already past 11 PM, move to tomorrow
        if (now.isAfter(todayAt12pm)) {
            todayAt12pm = todayAt12pm.plusDays(1)
        }
        return (Duration.between(now, todayAt12pm).toMillis())
    }

    fun endLootrun() {
        LootrunLogger.endRun()
        val data = getCurrentLootrunData() ?: return

        ScoreboardInfo.clearLootrunData() // this is data that is cleared every frame anyway
        data.currentMissionsActive.clear()
        data.activeCamp.camp.justCompleted()
        ConfigManager.INSTANCE.resetLootrun(Models.Character.id)

        mobHealthIncrease = 0
        mobSpeedIncrease = 0
        mobDamageIncrease = 0
        mobAttackSpeedIncrease = 0
        mobResistanceIncrease = 0
    }

    fun onClientTick(event: ClientTickEvent) {
        val data = getCurrentLootrunData() ?: return
        if (event.client.world != null && !currentBeaconOptionsFromWaypoints.isEmpty()) {
            // get the waypoint if we might be about to start it
            for (entry in currentBeaconOptionsFromWaypoints.entries) {
                if (entry.value == -1) {
                    data.possibleActiveBeaconColor = entry.key
                    break
                }
            }
            if (!beaconOptionsLastTick) {
                LootrunLogger.appendLine("new beacon options: ${currentBeaconOptionsFromWaypoints.keys}")
            }
        }
        beaconOptionsLastTick = !currentBeaconOptionsFromWaypoints.isEmpty()
        currentBeaconOptionsFromWaypoints.clear()
    }

    private var completeChaosCounter: Int = Int.MAX_VALUE
    fun onChatMessage(event: ChatMessageEvent) {
        val data = getCurrentLootrunData() ?: return
        if (completeChaosCounter != Int.MAX_VALUE) completeChaosCounter++

        if (data.status === LootrunStatus.PickingBeacon) {
            BeaconOptions.getMatches(event.cleanMessage)
        }

        for (part in event.splitMessage) {
            if (part == "Lootrun Completed") {
                onChallengeCompleted(data.activeBeaconColor)
                data.endStats
                    .addEndPulls(1) // assumes you did not fail the last challenge but doesn't really matter so
                config().endRewardsOverlay.updateDisplay()
                ChatMessageUtils.sendChatMessage(
                    "Lootrun completed with " + data.endStats
                        .endPulls + " pulls and " + getEffectivePulls() + " Effective pulls"
                )
                changeStatus(LootrunStatus.NotInLootrun)
            } else if (part == "Lootrun Failed") {
                changeStatus(LootrunStatus.NotInLootrun)
            } else if (part == "Choose a Beacon") {
                data.currentBeaconOptions.clear()
                changeStatus(LootrunStatus.PickingBeacon)
                data.beaconRerolls = 0
            } else if (part.startsWith("Challenge Failed!")) {
                onChallengeFailed()
            } else if (part == "Complete Chaos") {
                completeChaosCounter = 0
            }

            if (completeChaosCounter == 2) {
                for (opt in BeaconOptions.entries) {
                    if (part == opt.displayName) {
                        completeChaosChallengeCompleted(opt)
                    }
                }
            }

            val reRollsMatcher = beaconRerollPattern.matcher(part)
            if (reRollsMatcher.find()) {
                val rerollsLeft = reRollsMatcher.group(1).toInt()
                data.beaconRerolls = rerollsLeft
            }

            val endPullsMatcher = endPullsPattern.matcher(part)
            if (endPullsMatcher.find()) {
                val pullsGained = endPullsMatcher.group(1).toInt()
                data.endStats.addEndPulls(pullsGained)
            }

            val endSacsMatcher = sacrificePattern.matcher(part)
            if (endSacsMatcher.find()) {
                val sacsGained = endSacsMatcher.group(1).toInt()
                data.endStats.addEndSacs(sacsGained)
            }

            val endRerollsMatcher = endRerollPattern.matcher(part)
            if (endRerollsMatcher.find()) {
                val rrsGained = endRerollsMatcher.group(1).toInt()
                data.endStats.addEndRerolls(rrsGained)
            }
        }

        val curseMatcher = curseBuffPattern.matcher(event.asciiOnlyMessage)
        if (curseMatcher.find()) {
            var percent = curseMatcher.group(2).toInt()
            val negative = curseMatcher.group(1) == "-"
            if (negative) percent *= -1
            // TODO i think group 3 should be whether its curse or natural so i can separate and apply half curse effects if mission
            val type = curseMatcher.group(4)
            when (type.lowercase(Locale.getDefault())) {
                "health" -> {
                    addMobHealth(percent)
                    return
                }

                "resistance" -> {
                    addMobResistance(percent)
                    return
                }

                "damage" -> {
                    addMobDamage(percent)
                    return
                }

                "attack speed" -> {
                    addMobAttackSpeed(percent)
                    return
                }

                "walk speed" -> {
                    addMobSpeed(percent)
                    return
                }

                else -> {}
            }
        }
    }

    fun onScreenRender(event: ScreenRenderEvent) {
        val screen = event.screen
        if (screen !is HandledScreen<*>) return
        if (screen.getScreenHandler() !is GenericContainerScreenHandler) return
        val data = getCurrentLootrunData() ?: return

        val slot5: ItemStack = screen.getScreenHandler().getSlot(5).stack
        val slot4: ItemStack = screen.getScreenHandler().getSlot(4).stack

        val loreList: MutableList<Text> = ArrayList<Text>()

        val lore5 = slot5.getComponents().get(DataComponentTypes.LORE)
        if (lore5 != null) loreList.addAll(lore5.lines())

        val lore4 = slot4.getComponents().get(DataComponentTypes.LORE)
        if (lore4 != null) loreList.addAll(lore4.lines())

        for (text in loreList) {
            val line = text.string
            if (line.contains("Saved Pulls")) {
                // Extract number using regex
                val matcher = Pattern.compile("Saved Pulls: §f(\\d+)").matcher(line)
                if (matcher.find()) {
                    val savedPulls = matcher.group(1).toInt()
                    var camp = data.activeCamp
                    if (camp == null) {
                        // check if we are close to a camp (prevent wrong camp assignments)
                        val client = MinecraftClient.getInstance()
                        if (client == null || client.player == null || !Camps.isNearAnyCamp(
                                client.player!!.entityPos,
                                15.0
                            )
                        ) return
                        data.setActiveCamp()
                        camp = data.activeCamp
                        if (camp == null) {
                            WynnAdhocClient.LOGGER.error("Camp was null while sacs were on screen")
                            return
                        }
                    }
                    camp.camp.setPossibleSacs(savedPulls)
                }
            }
        }
    }

    fun onSlotClicked(event: SlotClickedEvent) {
        val data = getCurrentLootrunData() ?: return
        var closestCamp: Camps? = data.activeCamp
        if (event.player == null || event.handler == null) return

        if (!Camps.isNearAnyCamp(event.player.entityPos, 15.0)) return
        if (closestCamp == null) {
            data.setActiveCamp()
            closestCamp = data.activeCamp
            if (closestCamp == null) {
                WynnAdhocClient.LOGGER.error("Camp was null when sacs were clicked")
                return
            }
        }

        if (event.slotIndex >= 0 && event.slotIndex < event.handler.slots.size) {
            val slot: Slot = event.handler.slots[event.slotIndex]
            val clickedStack = slot.stack
            val tooltip = clickedStack.getTooltip(TooltipContext.DEFAULT, event.player, TooltipType.BASIC)
            if (tooltip.isEmpty()) return

            // 5 since there is always the claim button in 3 and no rerolls
            // might as well also check 4 even though you should never sac if you have rerolls left
            if (event.slotIndex == 5 || event.slotIndex == 4) {
                if (tooltip[0].string == "§a§lConfirm Sacrifice") {
                    closestCamp.camp.sac()
                }
            }
            //3 if only rerolls or only sacs and 2 if both
            // so you have to close the chest after claiming rewards so this is pointless
            if (event.slotIndex == 3 || event.slotIndex == 2) {
                if (tooltip[0].string == "§a§lConfirm Rewards") { // confirm rewards
                    closestCamp.camp.resetSacs()
                }
            }
            // when you have no sacs or rerolls this is the only button
            if (event.slotIndex == 4 && tooltip[0].string == "§c§lClose Chest") {
                closestCamp.camp.resetSacs()
            }
        }
    }
}