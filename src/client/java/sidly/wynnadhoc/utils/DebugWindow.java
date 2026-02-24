package sidly.wynnadhoc.utils;

import sidly.wynnadhoc.config.ConfigManager;
import sidly.wynnadhoc.config.catagories.DebugConfig;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DebugWindow {
    private static DebugConfig config() { return ConfigManager.INSTANCE.config.debug; }
    private static DebugWindow instance;

    private JFrame frame = null;
    private JTextArea textArea = null;
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private String lastMessage = "";


    public static DebugWindow getInstance() {
        // if we change the config from empty to not empty the window will not be null but not have a frame
        if (instance == null || (!config().shownDebugging.isEmpty() && instance.frame == null)) {
            instance = new DebugWindow();
        }
        return instance;
    }


    public DebugWindow() {
        if (config().shownDebugging.isEmpty()) return;

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
        frame.setVisible(true);
    }

    /** Logs a message to the popup window. */
    public void log(Priority level, String message) {
        if (!config().shownDebugging.contains(level)) return;
        if (textArea == null) return;

        SwingUtilities.invokeLater(() -> {
            if (Objects.equals(message, lastMessage)) return;
            String time = "[" + level + "] [" + LocalTime.now().format(timeFormat) + "] ";
            textArea.append(time + message + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
            lastMessage = message;
        });
    }

    /** Clears all messages from the log window. */
    public void clear() {
        if (textArea == null) return;
        SwingUtilities.invokeLater(() -> textArea.setText(""));
    }

    public enum Priority {
        INFO,
        WARNING,
        ERROR
    }
}

