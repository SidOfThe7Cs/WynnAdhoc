package sidly.wynnadhoc.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.DebugConfig;

public class Debug {
    private static DebugConfig config() {
        return ConfigManager.INSTANCE.config.debug;
    }

    private final Logger LOGGER;

    public Debug(String title) {
        LOGGER = LogManager.getLogger(title);
    }

    public void error(String message) {
        if (false) {
            log(Priority.ERROR, message);
        } else LOGGER.error(message);
    }

    public void warn(String message) {
        if (false) {
            log(Priority.WARNING, message);
        } else LOGGER.warn(message);
    }

    public void info(Type type, String message) {
        if (!config().shownDebugging.contains(type)) return;
        if (false) {
            log(Priority.INFO, message);
        } else LOGGER.info(type.get(message));
    }

    /**
     * Logs a message to the popup window.
     * doesn't use the info system or have colors
     *
     */
    @Deprecated // needs colors and type
    public void log(Priority level, String message) {
        /*
        if (config().newWindow) {
            frame.setVisible(true);
            SwingUtilities.invokeLater(() -> {
                if (Objects.equals(message, lastMessage)) return;
                String time = "[" + level + "] [" + LocalTime.now().format(timeFormat) + "] ";
                textArea.append(time + message + "\n");
                textArea.setCaretPosition(textArea.getDocument().getLength());
                lastMessage = message;
            });
        }

         */
    }

    public enum Priority {
        INFO,
        WARNING,
        ERROR
    }

    public enum Type {
        LOOTRUN,
        OUTER_VOID,
        CONFIG,
        MANUAL,
        WAR,
        TEMP,
        HUD,
        SERVER;

        public String get(String message) {
            return "[" + this.name() + "] " + message;
        }
    }
}

