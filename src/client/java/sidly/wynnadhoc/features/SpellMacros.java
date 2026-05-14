package sidly.wynnadhoc.features;

import com.wynntils.core.components.Models;
import com.wynntils.mc.event.ChangeCarriedItemEvent;
import com.wynntils.mc.event.SetSlotEvent;
import com.wynntils.models.character.type.ClassType;
import com.wynntils.models.spells.event.SpellEvent;
import com.wynntils.models.spells.type.SpellDirection;
import com.wynntils.models.spells.type.SpellFailureReason;
import com.wynntils.models.spells.type.SpellType;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.mc.MouseUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import sidly.wynnadhoc.WynnAdhocClient;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.SpellConfig;
import sidly.wynnadhoc.config.gui.HudElementManager;
import sidly.wynnadhoc.config.gui.TextHudElement;
import sidly.wynnadhoc.event.KeyboardEvent;
import sidly.wynnadhoc.event.MouseButtonEvent;
import sidly.wynnadhoc.event.WorldChangeEvent;
import sidly.wynnadhoc.utils.Debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO
//  it miscasts alot when holding down
//  fix double left click and add hold left click
//  fix archer misscast i think?
public class SpellMacros {
    private static SpellConfig config() {
        return ConfigManager.INSTANCE.config.spell;
    }

    private static final List<SpellCast> queue = new ArrayList<>();
    private static Long lastUpdate = 0L;
    private static boolean isUpdating = false;
    private static SpellDirection[] lastPartial = null;

    private static final boolean DEBUG = true;

    public static void register() {
        HudElementManager.register(new TextHudElement(
                config().spellQueueOverlay,
                SpellMacros::shouldShowSpellQueueOverlay,
                SpellMacros::updateSpellQueueOverlay)
        );
    }

    private static String updateSpellQueueOverlay() {
        return queue.stream().map(e -> {
            SpellType type = e.getSpellType();
            return type == null ? "Main Attack" : type.getName();
        }).collect(Collectors.joining("\n"));
    }

    private static Boolean shouldShowSpellQueueOverlay() {
        return config().showQueueDisplay && !queue.isEmpty();
    }

    private static void checkHeldKeys() {
        Window window = MinecraftClient.getInstance().getWindow();

        if (InputUtil.isKeyPressed(window, config().firstSpellCast)) {
            if (queue.size() < config().maxQueueSize) {
                if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "adding first spell from held");
                queue.add(SpellCast.FIRST);
                start();
            }
        }

        if (InputUtil.isKeyPressed(window, config().secondSpellCast)) {
            if (queue.size() < config().maxQueueSize) {
                if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "adding second spell from held");
                queue.add(SpellCast.SECOND);
                start();
            }
        }

        if (InputUtil.isKeyPressed(window, config().thirdSpellCast)) {
            if (queue.size() < config().maxQueueSize) {
                if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "adding third spell from held");
                queue.add(SpellCast.THIRD);
                start();
            }
        }

        if (InputUtil.isKeyPressed(window, config().fourthSpellCast)) {
            if (queue.size() < config().maxQueueSize) {
                if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "adding fourth spell from held");
                queue.add(SpellCast.FOURTH);
                start();
            }
        }

        if (MinecraftClient.getInstance().options.attackKey.isPressed() && config().reRouteMainAttacks) {
            if (queue.size() < config().maxQueueSize) {
                if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "adding main attack from held");
                queue.add(SpellCast.MAIN);
                start();
            }
        }
    }

    public static void onTick() {
        // check for wynntils failing to recognize spell (like arcane transfer)
        SpellDirection[] lastSpell = Models.Spell.getLastSpell();
        if (!Arrays.equals(lastSpell, lastPartial)) {
            onUpdate(lastSpell);
        }
        lastPartial = lastSpell;

        // backup
        Long now = System.currentTimeMillis();
        if (now - lastUpdate > 1500) {
            isUpdating = false;
            start();
        }
    }

    public static void onUpdate(SpellDirection[] partial) {
        if (partial.length == 3) {
            if (DEBUG)
                WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "calling complete from update as new partial is of len 3");
            onComplete(SpellType.fromSpellDirectionArray(partial));
        }
    }

    public static void onPartial(SpellEvent.Partial event) {
        if (queue.isEmpty()) return;
        isUpdating = true;
        SpellDirection[] current = event.getSpellDirectionArray();
        if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "new partial: " + Arrays.toString(current));
        click(queue.getFirst().getNext(current));
    }

    public static void onCastEvent(SpellEvent.Cast event) {
        onComplete(event.getSpellType());
    }

    public static void onComplete(SpellType type) {
        if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "onComplete: " + type);
        if (queue.isEmpty()) return;
        SpellType target = queue.getFirst().getSpellType();
        if (type != null && target != null && type.getSpellNumber() == target.getSpellNumber()) {
            queue.removeFirst();
        }
        isUpdating = false;
        if (queue.isEmpty()) checkHeldKeys();
        start();
    }

    public static void onFail(SpellEvent.Failed event) {
        if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "onFail");
        if (queue.isEmpty()) return;
        if (event.getFailureReason() == SpellFailureReason.NOT_UNLOCKED) {
            queue.removeFirst();
            start();
        }
    }

    public static void onWorldChange(WorldChangeEvent event) {
        if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "clearing on world change");
        clearQueue();
    }

    public static void onItemSwap(ChangeCarriedItemEvent event) {
        if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "clearing on held item change");
        clearQueue();
    }

    public static void onSetSlotEvent(SetSlotEvent.Post event) {
        if (event.getSlot() != McUtils.inventory().getSelectedSlot()) return;
        if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "clearing on set slot");
        clearQueue();
    }

    public static void onKeyPressed(KeyboardEvent event) {
        if (event.action != 1 || queue.size() >= config().maxQueueSize) return;
        if (event.key == config().firstSpellCast) {
            if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "adding first spell from click");
            queue.add(SpellCast.FIRST);
            start();
        } else if (event.key == config().secondSpellCast) {
            if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "adding second spell from click");
            queue.add(SpellCast.SECOND);
            start();
        } else if (event.key == config().thirdSpellCast) {
            if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "adding third spell from click");
            queue.add(SpellCast.THIRD);
            start();
        } else if (event.key == config().fourthSpellCast) {
            if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "adding fourth spell from click");
            queue.add(SpellCast.FOURTH);
            start();
        }
    }

    public static void onMouseButton(MouseButtonEvent event) {
        if (!config().reRouteMainAttacks) return;
        if (isUpdating) event.canceled = true;
        if (event.isLeftClick() && event.isPress() &&
                MinecraftClient.getInstance().currentScreen == null &&
                MinecraftClient.getInstance().player.getInventory().getSelectedStack() != null &&
                queue.size() < config().maxQueueSize
        ) {
            if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "adding main attack from click");
            queue.add(SpellCast.MAIN);
            start();
        }
    }

    public static void click(SpellDirection dir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (dir == null || client == null || client.world == null || client.currentScreen != null) return;
        if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "clicking: " + dir);
        lastUpdate = System.currentTimeMillis();
        if (dir == SpellDirection.RIGHT) MouseUtils.sendRightClickInput();
        else MouseUtils.sendLeftClickInput();
    }

    public static void start() {
        config().spellQueueOverlay.updateDisplay();
        if (isUpdating || queue.isEmpty()) return;
        click(queue.getFirst().getNext(new SpellDirection[0]));
        if (queue.getFirst() == SpellCast.MAIN) {
            // handle main attack removal and just assume it worked since main attacks dont have spell cast overlay
            queue.removeFirst();
        }
    }

    public static void clearQueue() {
        queue.clear();
        config().spellQueueOverlay.updateDisplay();
        isUpdating = false;
    }

    public static class SpellCast {
        private SpellDirection[] target = new SpellDirection[3];

        private SpellCast(int left) {
            target = new SpellDirection[1];
            target[0] = SpellDirection.values()[left];
        }

        private SpellCast(int a, int b, int c) {
            try {
                target[0] = SpellDirection.values()[a];
                target[1] = SpellDirection.values()[b];
                target[2] = SpellDirection.values()[c];
            } catch (Exception e) {
                WynnAdhocClient.LOGGER.warn("spell caster had an error: " + e.getMessage());
            }
        }

        public SpellDirection[] getTarget() {
            if (Models.Character.getClassType().equals(ClassType.ARCHER)) {
                return SpellDirection.invertArray(target);
            } else return target;
        }

        public SpellDirection getNext(SpellDirection[] current) {

            if (current.length >= getTarget().length) return null;
            if (isPossible(current)) return getTarget()[current.length];

            Optional<SpellCast> newTarget = config().misscastPrioList.stream()
                    .filter(t -> t.getClassType() == Models.Character.getClassType())
                    .map(SpellCast::fromType)
                    .filter(c -> c != null && c.isPossible(current))
                    .findFirst();

            SpellDirection spellDirection = newTarget.map(spellCast -> spellCast.getTarget()[current.length]).orElse(null);
            if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "getting next: " + spellDirection);
            return spellDirection;
        }

        public boolean isPossible(SpellDirection[] current) {
            boolean possible = true;

            for (int i = 0; i < current.length; i++) {
                if (current[i] != getTarget()[i]) {
                    possible = false;
                    break;
                }
            }

            return possible;
        }

        public SpellType getSpellType() {
            return SpellType.fromSpellDirectionArray(target);
        }

        public static SpellCast fromType(SpellType type) {
            int spellNumber = type.getSpellNumber();
            return switch (spellNumber) {
                case 1 -> FIRST;
                case 2 -> SECOND;
                case 3 -> THIRD;
                case 4 -> FOURTH;
                default -> null;
            };
        }

        public static SpellCast FIRST = new SpellCast(0, 1, 0);
        public static SpellCast SECOND = new SpellCast(0, 0, 0);
        public static SpellCast THIRD = new SpellCast(0, 1, 1);
        public static SpellCast FOURTH = new SpellCast(0, 0, 1);
        public static SpellCast MAIN = new SpellCast(1);
    }
}
