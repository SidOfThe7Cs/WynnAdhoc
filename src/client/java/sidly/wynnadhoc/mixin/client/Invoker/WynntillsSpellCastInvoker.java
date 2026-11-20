package sidly.wynnadhoc.mixin.client.Invoker;

import com.wynntils.features.combat.QuickCastFeature;
import com.wynntils.models.spells.type.CombatClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(QuickCastFeature.class)
public interface WynntillsSpellCastInvoker {
    @Invoker("tryCastSpell")
    boolean invokeTryCastSpell(List<CombatClickType> clicks, boolean notifyInvalidWeapon);
}
