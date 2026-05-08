package sidly.wynnadhoc.features;

import com.wynntils.core.components.Models;
import com.wynntils.models.spells.type.SpellDirection;
import com.wynntils.utils.mc.MouseUtils;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.SpellConfig;
import sidly.wynnadhoc.event.ClientTickEvent;
import sidly.wynnadhoc.event.KeyboardEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO
//  display current queue
//  add config for limit
//  safe cast
//  check if world is null
public class SpellMacros {
    private static SpellConfig config() {
        return ConfigManager.INSTANCE.config.spell;
    }

    private static final List<SpellCast> queue = new ArrayList<>();
    private static SpellDirection[] last = SpellDirection.NO_SPELL;

    public static void onTick(ClientTickEvent empty) {
        SpellDirection[] current = Models.Spell.getLastSpell();

        if (!Arrays.equals(last, current)) {
            onComboUpdate(current);
        }

        last = current;
    }

    public static void onComboUpdate(SpellDirection[] current) {
        WynnAdhocClient.LOGGER.temp("combo update: " + Arrays.toString(current));
        if (queue.isEmpty()) return;
        click(queue.getFirst().getNext(current));
        if (current.length == 2) queue.removeFirst();
    }

    public static void onKeyPressed(KeyboardEvent event) {
        if (event.action != 1) return;
        if (event.key == config().firstSpellCast) {
            queue.add(SpellCast.FIRST);
            start();
        } else if (event.key == config().secondSpellCast) {
            queue.add(SpellCast.SECOND);
            start();
        }
    }

    public static void click(SpellDirection dir) {
        if (dir == null) return;
        if (dir == SpellDirection.RIGHT) MouseUtils.sendRightClickInput();
        else MouseUtils.sendLeftClickInput();
    }

    public static void start() {
        if (queue.size() != 1) return;
        click(queue.getFirst().getNext(new SpellDirection[0]));
    }

    public static class SpellCast {
        private final SpellDirection[] target = new SpellDirection[3];

        private SpellCast(int a, int b, int c) {
            try {
                target[0] = SpellDirection.values()[a];
                target[1] = SpellDirection.values()[b];
                target[2] = SpellDirection.values()[c];
            } catch (Exception e) {
                WynnAdhocClient.LOGGER.warn("spell caster had an error: " + e.getMessage());
            }
        }

        public SpellDirection getNext(SpellDirection[] current) {
            if (current.length >= target.length) return null;
            return target[current.length];
        }

        // TODO archer inverse
        public static SpellCast FIRST = new SpellCast(0, 1, 1);
        public static SpellCast SECOND = new SpellCast(0, 0, 0);
    }
}
