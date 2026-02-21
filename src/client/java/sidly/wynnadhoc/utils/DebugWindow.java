package sidly.wynnadhoc.utils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DebugWindow {
    private static DebugWindow instance;
    private final JTextArea textArea;
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
    private String lastMessage = "";

    public static DebugWindow getInstance() {
        // Todo config
        if (instance == null) {
            instance = new DebugWindow();
        }
        return instance;
    }


    public DebugWindow() {
        // Todo config

        JFrame frame = new JFrame("WynnTools Debug Log");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    /** Logs a message to the popup window. */
    public void log(Priority level, String message) {
        // Todo config
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
        ERROR,
        NONE
    }
}

