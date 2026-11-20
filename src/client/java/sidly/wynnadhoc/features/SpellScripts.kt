package sidly.wynnadhoc.features

import com.mojang.brigadier.context.CommandContext
import com.wynntils.core.components.Models
import com.wynntils.features.combat.QuickCastFeature
import com.wynntils.models.spells.type.CombatClickType
import com.wynntils.models.spells.type.SpellType
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.MinecraftClient
import sidly.wynnadhoc.event.CommandRegistrationEvent
import sidly.wynnadhoc.mixin.client.Invoker.WynntillsSpellCastInvoker

// probably doesnt handle archer but idc
object SpellScripts {

    private val FIRST_SPELL_SEQUENCE: List<CombatClickType> =
        listOf(CombatClickType.PRIMARY, CombatClickType.SECONDARY, CombatClickType.PRIMARY)
    private val SECOND_SPELL_SEQUENCE: List<CombatClickType> =
        listOf(CombatClickType.PRIMARY, CombatClickType.PRIMARY, CombatClickType.PRIMARY)
    private val THIRD_SPELL_SEQUENCE: List<CombatClickType> =
        listOf(CombatClickType.PRIMARY, CombatClickType.SECONDARY, CombatClickType.SECONDARY)
    private val FOURTH_SPELL_SEQUENCE: List<CombatClickType> =
        listOf(CombatClickType.PRIMARY, CombatClickType.PRIMARY, CombatClickType.SECONDARY)

    class SpellScript {
        val spellList: MutableList<List<CombatClickType>> = mutableListOf()
        var index = 0

        fun add(spell: SpellType): SpellScript {
            when (spell.spellNumber) {
                1 -> FIRST_SPELL_SEQUENCE
                2 -> SECOND_SPELL_SEQUENCE
                3 -> THIRD_SPELL_SEQUENCE
                4 -> FOURTH_SPELL_SEQUENCE
                else -> {
                    null
                }
            }?.let { spellList.add(it) }
            return this
        }

        fun getNext(): List<CombatClickType> {
            val oldIndex = index
            if (index >= spellList.size - 1) {
                index = 0
            } else index++
            return spellList[oldIndex]
        }

        fun castNext() {
            (QuickCastFeature() as WynntillsSpellCastInvoker).invokeTryCastSpell(getNext(), true)
        }

        fun reset() {
            index = 0
        }
    }

    var runningScript: SpellScript? = null

    val warHealerScript = SpellScript()
        .add(SpellType.TOTEM)
        .add(SpellType.AURA)
        .add(SpellType.AURA)
        .add(SpellType.AURA)
        .add(SpellType.AURA)
        .add(SpellType.AURA)


    fun registerCommands(event: CommandRegistrationEvent) {
        event.register(
            ClientCommandManager.literal("SpellScripts")
                .then(
                    ClientCommandManager.literal("warHealer")
                        .executes { _: CommandContext<FabricClientCommandSource> ->
                            runningScript = warHealerScript
                            1
                        }
                )
        )
    }

    fun onTick() {
        if (runningScript != null) {
            if (MinecraftClient.getInstance().currentScreen != null) {
                runningScript?.reset()
                runningScript = null
                return
            }

            if (!Models.SpellCaster.isBusy) {
                runningScript?.castNext()
            }
        }
    }
}