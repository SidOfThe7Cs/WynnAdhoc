package sidly.wynnadhoc.features;

import com.wynntils.core.components.Models;
import com.wynntils.models.character.type.ClassType;
import com.wynntils.models.spells.event.SpellEvent;
import com.wynntils.models.spells.type.SpellDirection;
import com.wynntils.models.spells.type.SpellFailureReason;
import com.wynntils.models.spells.type.SpellType;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO
//  check if world is null
//  on world change
//  handle main attacks
//  check screen null
//  button to clear queue
//  fix archer misscast i think?
public class SpellMacros {
    private static SpellConfig config() {
        return ConfigManager.INSTANCE.config.spell;
    }

    private static final List<SpellCast> queue = new ArrayList<>();
    private static Long lastUpdate = 0L;
    private static boolean isUpdating = false;

    public static void register() {
        HudElementManager.register(new TextHudElement(
                config().spellQueueOverlay,
                SpellMacros::shouldShowSpellQueueOverlay,
                SpellMacros::updateSpellQueueOverlay)
        );
    }

    private static String updateSpellQueueOverlay() {
        return queue.stream().map(e -> e.getSpellType().getName()).collect(Collectors.joining("\n"));
    }

    private static Boolean shouldShowSpellQueueOverlay() {
        return config().showQueueDisplay && !queue.isEmpty();
    }

    private static int firstCounter = 0;
    private static int secondCounter = 0;
    private static int thirdCounter = 0;
    private static int fourthCounter = 0;

    public static void onTick() {
        Window window = MinecraftClient.getInstance().getWindow();

        if (InputUtil.isKeyPressed(window, config().firstSpellCast)) {
            if (firstCounter >= config().holdToCastDelay) {
                if (queue.size() < config().maxQueueSize) {
                    queue.add(SpellCast.FIRST);
                    start();
                }
            } else firstCounter++;
        } else firstCounter = 0;

        if (InputUtil.isKeyPressed(window, config().secondSpellCast)) {
            if (secondCounter >= config().holdToCastDelay) {
                if (queue.size() < config().maxQueueSize) {
                    queue.add(SpellCast.SECOND);
                    start();
                }
            } else secondCounter++;
        } else secondCounter = 0;

        if (InputUtil.isKeyPressed(window, config().thirdSpellCast)) {
            if (thirdCounter >= config().holdToCastDelay) {
                if (queue.size() < config().maxQueueSize) {
                    queue.add(SpellCast.THIRD);
                    start();
                }
            } else thirdCounter++;
        } else thirdCounter = 0;

        if (InputUtil.isKeyPressed(window, config().fourthSpellCast)) {
            if (fourthCounter >= config().holdToCastDelay) {
                if (queue.size() < config().maxQueueSize) {
                    queue.add(SpellCast.FOURTH);
                    start();
                }
            } else fourthCounter++;
        } else fourthCounter = 0;

        if (queue.isEmpty()) return;
        Long now = System.currentTimeMillis();
        if (now - lastUpdate > 1500) {
            isUpdating = false;
            start();
        }
    }

    public static void onPartial(SpellEvent.Partial event) {
        if (queue.isEmpty()) return;
        isUpdating = true;
        SpellDirection[] current = event.getSpellDirectionArray();
        click(queue.getFirst().getNext(current));
    }

    public static void onComplete(SpellEvent.Cast event) {
        if (queue.isEmpty()) return;
        SpellType target = queue.getFirst().getSpellType();
        if (event.getSpellType().equals(target)) {
            queue.removeFirst();
            isUpdating = false;
        }
        start();
    }

    public static void onFail(SpellEvent.Failed event) {
        if (event.getFailureReason() == SpellFailureReason.NOT_UNLOCKED) {
            queue.removeFirst();
            start();
        }
    }

    public static void onKeyPressed(KeyboardEvent event) {
        if (event.action != 1 || queue.size() >= config().maxQueueSize) return;
        if (event.key == config().firstSpellCast) {
            queue.add(SpellCast.FIRST);
            start();
        } else if (event.key == config().secondSpellCast) {
            queue.add(SpellCast.SECOND);
            start();
        } else if (event.key == config().thirdSpellCast) {
            queue.add(SpellCast.THIRD);
            start();
        } else if (event.key == config().fourthSpellCast) {
            queue.add(SpellCast.FOURTH);
            start();
        }
    }

    public static void click(SpellDirection dir) {
        if (dir == null) return;
        lastUpdate = System.currentTimeMillis();
        if (dir == SpellDirection.RIGHT) MouseUtils.sendRightClickInput();
        else MouseUtils.sendLeftClickInput();
    }

    public static void start() {
        if (isUpdating || queue.isEmpty()) return;
        config().spellQueueOverlay.updateDisplay();
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

            return newTarget.map(spellCast -> spellCast.getTarget()[current.length]).orElse(null);
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
    }
}
