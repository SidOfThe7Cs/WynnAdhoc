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

import java.util.*;
import java.util.stream.Collectors;

// TODO
//  dont use vanilla keybinds for main attack (ahhhhhhh)
//  fix archer
public class SpellMacros {
    private static SpellConfig config() {
        return ConfigManager.INSTANCE.config.spell;
    }

    private static final List<SpellCast> queue = new ArrayList<>();
    private static Long lastUpdate = 0L;
    private static boolean isUpdating = false;
    private static SpellDirection[] lastPartial = null;
    private static final Queue<Runnable> clickQueue = new LinkedList<>();

    private static final boolean DEBUG = true;

    public static void register() {
        HudElementManager.register(new TextHudElement(
                config().spellQueueOverlay,
                SpellMacros::shouldShowSpellQueueOverlay,
                SpellMacros::updateSpellQueueOverlay)
        );
    }

    private static String updateSpellQueueOverlay() {
        if (!config().toggleSpellcaster) return "";
        return queue.stream().map(e -> {
            SpellType type = e.getSpellType();
            return type == null ? "Main Attack" : type.getName();
        }).collect(Collectors.joining("\n"));
    }

    private static Boolean shouldShowSpellQueueOverlay() {
        return config().showQueueDisplay && !queue.isEmpty();
    }

    private static void checkHeldKeys() {
        if (!config().toggleSpellcaster) return;
        Window window = MinecraftClient.getInstance().getWindow();

        if (InputUtil.isKeyPressed(window, config().firstSpellCast)) {
            if (queue.size() < config().maxQueueSize) {
                if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "adding first spell from held");
                queue.add(SpellCast.FIRST);
            }
        }

        if (InputUtil.isKeyPressed(window, config().secondSpellCast)) {
            if (queue.size() < config().maxQueueSize) {
                if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "adding second spell from held");
                queue.add(SpellCast.SECOND);
            }
        }

        if (InputUtil.isKeyPressed(window, config().thirdSpellCast)) {
            if (queue.size() < config().maxQueueSize) {
                if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "adding third spell from held");
                queue.add(SpellCast.THIRD);
            }
        }

        if (InputUtil.isKeyPressed(window, config().fourthSpellCast)) {
            if (queue.size() < config().maxQueueSize) {
                if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "adding fourth spell from held");
                queue.add(SpellCast.FOURTH);
            }
        }

        checkMainAttack();
    }

    public static void checkMainAttack() {
        if (!config().toggleSpellcaster) return;
        if (MinecraftClient.getInstance().options.attackKey.isPressed() && config().toggleSpellcaster) {
            if (queue.size() < config().maxQueueSize) {
                if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "adding main attack from held");
                queue.add(SpellCast.MAIN);
            }
        }
    }

    public static void onTick() {
        if (!config().toggleSpellcaster) {
            if (!queue.isEmpty()) clearQueue();
            return;
        }

        if (!clickQueue.isEmpty()) {
            clickQueue.poll().run();
            if (clickQueue.size() > 5)
                WynnAdhocClient.LOGGER.warn("click queue has " + clickQueue.size() + " entries stack overflow possible");
        }

        // check for spells that dont show there name when cast (like arcane transfer)
        SpellDirection[] lastSpell = Models.Spell.getLastSpell();
        if (!Arrays.equals(lastSpell, lastPartial)) {
            onUpdate(lastSpell);
        }
        lastPartial = lastSpell;

        if (queue.isEmpty()) {
            checkMainAttack();
            start();
        }

        // backup
        Long now = System.currentTimeMillis();
        if (now - lastUpdate > config().minTimeout) {
            if (DEBUG && !queue.isEmpty())
                WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "timeout, restarting queue");
            isUpdating = false;
            start();
        }

        config().spellQueueOverlay.updateDisplay();
    }

    public static void onUpdate(SpellDirection[] partial) {
        if (!config().toggleSpellcaster) return;
        if (partial.length == 3) {
            if (DEBUG)
                WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "calling complete from update as new partial is of len 3");
            onComplete(SpellType.fromSpellDirectionArray(partial));
        }
    }

    public static void onPartial(SpellEvent.Partial event) {
        if (!config().toggleSpellcaster) return;
        if (queue.isEmpty()) return;
        isUpdating = true;
        SpellDirection[] current = event.getSpellDirectionArray();
        if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "new partial: " + Arrays.toString(current));
        click(queue.getFirst().getNext(current));
    }

    public static void onCastEvent(SpellEvent.Cast event) {
        if (!config().toggleSpellcaster) return;
        onComplete(event.getSpellType());
    }

    public static void onComplete(SpellType type) {
        if (!config().toggleSpellcaster) return;
        if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "onComplete: " + type);
        isUpdating = false;
        lastPartial = new SpellDirection[0];
        if (queue.isEmpty()) return;
        SpellType target = queue.getFirst().getSpellType();
        if (type != null && target != null && type.getSpellNumber() == target.getSpellNumber()) {
            queue.removeFirst();
        }
        if (queue.isEmpty()) checkHeldKeys();
        start();
    }

    public static void onFail(SpellEvent.Failed event) {
        if (!config().toggleSpellcaster) return;
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
        if (!config().toggleSpellcaster) return;
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
        if (!config().toggleSpellcaster) return;
        if (isUpdating) event.canceled = true;
        if (event.isLeftClick() && event.isPress() &&
                MinecraftClient.getInstance().currentScreen == null &&
                MinecraftClient.getInstance().player.getInventory().getSelectedStack() != null &&
                queue.size() < config().maxQueueSize &&
                lastPartial.length == 0
        ) {
            if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "adding main attack from click");
            queue.add(SpellCast.MAIN);
            start();
        }
    }

    public static void click(SpellDirection dir) {
        if (!config().toggleSpellcaster) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (dir == null || client == null || client.world == null || client.currentScreen != null)
            return;
        if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "clicking: " + dir);

        lastUpdate = System.currentTimeMillis();

        if (dir == SpellDirection.RIGHT)
            clickQueue.add(MouseUtils::sendRightClickInput);
        else clickQueue.add(MouseUtils::sendLeftClickInput);
    }

    public static void start() {
        if (!config().toggleSpellcaster) return;
        if (isUpdating || queue.isEmpty()) return;
        if (queue.getFirst() == SpellCast.MAIN) {
            if (DEBUG)
                WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "onStart next = main last partial: " + Arrays.toString(lastPartial));
            if (lastPartial.length == 0 || lastPartial.length == 3) {
                // handle main attack removal and just assume it worked since main attacks dont have spell cast overlay
                if (DEBUG) WynnAdhocClient.LOGGER.info(Debug.Type.SPELL, "onStart removing main attack from queue");
                click(queue.getFirst().getNext(new SpellDirection[0]));
                queue.removeFirst();
            }
        } else {
            click(queue.getFirst().getNext(new SpellDirection[0]));
            isUpdating = true;
        }
    }

    public static void clearQueue() {
        queue.clear();
        clickQueue.clear();
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
