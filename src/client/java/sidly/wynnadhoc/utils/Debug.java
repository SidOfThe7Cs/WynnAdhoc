package sidly.wynnadhoc.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.DebugConfig;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Debug {
    private static DebugConfig config() {
        return ConfigManager.INSTANCE.config.debug;
    }

    private final JFrame frame;
    private final JTextArea textArea;
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private String lastMessage = "";
    private final Logger LOGGER;

    public Debug(String title) {
        LOGGER = LogManager.getLogger(title);

        frame = new JFrame("WynnAdhoc Debug Log");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setAutoRequestFocus(false);
    }

    public void error(String message) {
        if (config().newWindow) {
            log(Priority.ERROR, message);
        } else LOGGER.error(message);
    }

    public void warn(String message) {
        if (config().newWindow) {
            log(Priority.WARNING, message);
        } else LOGGER.warn(message);
    }

    public void info(Type type, String message) {
        if (!config().shownDebugging.contains(type)) return;
        if (config().newWindow) {
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
    }

    /**
     * Clears all messages from the log window.
     */
    public void clear() {
        if (textArea == null) return;
        SwingUtilities.invokeLater(() -> textArea.setText(""));
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
        HUD;

        public String get(String message) {
            return "[" + this.name() + "] " + message;
        }
    }
}

